package com.backend.bankingapplication.security.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimService {

    private final TokenService tokenService;

    public Long getUserId(String token) {
        return getClaimByName(token, TokenService.USER_ID, Long.class);
    }

    public <T extends Serializable> T getClaimByName(String token, String claim, Class<T> clazz) {
        Claims claims = getClaims(token);
        return claims.get(claim, clazz);
    }

    private Claims getClaims(String token) {
        try {
            Jws<Claims> claims = tokenService.parseToken(token);
            return claims.getBody();
        } catch (JwtException | IllegalArgumentException exception) {
            log.warn("Failed to parse token, message {}", exception.getMessage());
            throw exception;
        }
    }
}
