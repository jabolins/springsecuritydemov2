package com.example.springsecuritydemov2.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * aklase kas filtrēs visus pieteikumus un palaidīs tālāk tikai tos kam atbilst tokens
 */
@Component
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest
            , ServletResponse servletResponse
            , FilterChain filterChain) throws IOException, ServletException {

        String token = jwtTokenProvider.resolveToken((HttpServletRequest) servletRequest);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) { // ja tokens nav tukšs un mūsu izveidotais tokenProvaider validē to tad..
                Authentication authentication = jwtTokenProvider.getAuthentication(token); // mēs iegūstam authentification priekš konkrētā tokena
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication); // securityContextHolder ieliek savā kontekstā mūsu authentification
                }
            }
        } catch (JwtAuthorizationException e) {
            SecurityContextHolder.clearContext(); // ja sanāca izņēmums tad jānotīra securityContextHolder
            ((HttpServletResponse) servletResponse).sendError(e.getHttpStatus().value()); // un nosūtam atbildi norādot gan saņemto response gan statusu uz to
            throw new JwtAuthorizationException("JWT is expired or invalid"); // tas ir mums lai mēs redzētu kas noticis
        }
        filterChain.doFilter(servletRequest, servletResponse); // ja viss kārtībā tad lai filterChain nodod tālāk servetRequest un servletResponse
    }
}
