package com.example.springsecuritydemov2.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")// šādi norādam ka vērtība jāņem no application.properties
    private String secretKey;
    @Value("${jwt.header}")
    private String authorizationHeader;
    @Value("${jwt.expiration}")
    private Long validityInMilliseconds;

    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) { // atsauksme
        // uz mūsu izveodot UserDetailServiceImpl
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() { // šīs ir drošības prasības aprakstītas dokumentācijā
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, String role) { // metode tokena izveidei
        Claims claims = Jwts.claims().setSubject(username); //Calaims ir map kurā varam salikt savus laukus kādus vēlamies
        claims.put("role", role);
        Date now = new Date(); // izmantosim lai piefiksētu kad tokens ir izveidots
        Date validity = new Date(now.getTime() + validityInMilliseconds * 1000); // laiks milisekundēs no izveidošanas brīža cik ilgi būs derīgs token

        return Jwts.builder() // izveidojam JWT tokenu
                .setClaims(claims) // lauki ko izveodojām augstāk
                .setIssuedAt(now) //izveidošanas laiks
                .setExpiration(validity) //derīguma termiņš milisekundēs
                .signWith(SignatureAlgorithm.HS256, secretKey) // ar kādu metodi šifrējam un kaut kāds slepeneais vārds priekš šifrēšanas
                .compact();
    }

    public boolean validateToken(String token) { // metode tokena validācijai
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before((new Date())); // pārbauda vai termiņš tokenam nav izgājis
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthorizationException("Jwt token is expired or invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    public Authentication getAuthentication(String token) { // šis tiks izmantots priekš autorizācijas (vismaz tā es to saprotu)
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token)); // atrod lietotāju pēc usarneme ko savkurār dabū no token
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());// dabūjam lietotāja datus un pilnvaras (getAuthorities)
    }

    public String getUsername(String token) { // šādi mēs no token dabūjam username (jo to bijām izmanotojuši veidojot token)
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {//saņem HTTP pieprasījumu
        return request.getHeader(authorizationHeader);
    }
}

