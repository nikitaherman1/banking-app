package com.backend.bankingapplication.security.service.impl;

import com.backend.bankingapplication.security.model.AuthorizedUserDetails;
import com.backend.bankingapplication.security.provider.RSAKeyProvider;
import com.backend.bankingapplication.core.exception.TokenCreationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    public static final String USER_ID = "user_id";

    @Value("${token.expiration}")
    private Long expiration;

    private final RSAKeyProvider rsaKeyProvider;

    public String generateToken(AuthorizedUserDetails userDetails) {
        long currentTimeMillis = System.currentTimeMillis();

        try {
            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setClaims(createClaims(userDetails))
                    .setIssuedAt(new Date(currentTimeMillis))
                    .setExpiration(new Date(currentTimeMillis + expiration))
                    .signWith(rsaKeyProvider.getRsaPrivateKey())
                    .compact();
        } catch (InvalidKeyException exception) {
            log.error("Invalid private key when generating token, error message {}", exception.getMessage());
            throw new TokenCreationException("Invalid private key when generating token");
        }
    }

    public Map<String, Object> createClaims(AuthorizedUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USER_ID, userDetails.getUserId());

        return claims;
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(rsaKeyProvider.getRsaPublicKey())
                .build()
                .parseClaimsJws(token);
    }
}
