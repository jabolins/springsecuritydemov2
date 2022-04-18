package com.example.springsecuritydemov2.security;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException; // jāpievērš uzmanība lai būtu tieši šis Authorization Exception
@Getter
public class JwtAuthorizationException extends AuthenticationException {
    private HttpStatus httpStatus; // tas ir lai būtu "user frendly"

    public JwtAuthorizationException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }

    public JwtAuthorizationException(String msg) {
        super(msg);
    }
}
