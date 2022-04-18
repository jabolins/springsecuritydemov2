package com.example.springsecuritydemov2.rest;

import com.example.springsecuritydemov2.model.User;
import com.example.springsecuritydemov2.repository.UserRepository;
import com.example.springsecuritydemov2.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * šī ir kontrolieris kur darbosies  kā filtrs autoriācijai. cauri laidīs tikai autorizētus lietotājus
 * (izmantos AuthenticRequestDTO esošo username un password. To izveidojām jo gribam lietot ar JWT token
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private UserRepository userRepository; // lai piekļūtu datu bāzes datiem
    private JwtTokenProvider jwtTokenProvider;  // lai izveidotu tokenu

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));  // authentifikatoram uzdevums autentificēt lietotāju ar UsernamePasss... palidzību un nodod tur username un request.getPasswird
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Lietotājs nav atrasts")); // izveido mūsu iekšējo User no datu bāze, bet ja tāda tur nav parāda izņe'mumu
            String token = jwtTokenProvider.createToken(request.getUsername(), user.getRole().name()); // ja viss veiksmīgi, izveidojam tokenu  ar mūsu leitotājvārdu un lomu

            Map<Object, Object> response = new HashMap<>(); // gatavoju objektu atbildes sniegšanai
            response.put("username", request.getUsername());
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Nepareizs lietotājvārds vai parole", HttpStatus.FORBIDDEN); // ja nesanāca, atdodam kļūdas paziņojumu statusu FORBIDDEN
        }
    }


    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, null);
    }

}
