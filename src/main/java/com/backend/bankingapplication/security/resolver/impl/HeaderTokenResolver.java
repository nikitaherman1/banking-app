package com.backend.bankingapplication.security.resolver.impl;

import com.backend.bankingapplication.security.resolver.TokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class HeaderTokenResolver implements TokenResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public String resolve(HttpServletRequest request) {
        String authorizationValue = request.getHeader(AUTHORIZATION_HEADER);
        if (null == authorizationValue || authorizationValue.isEmpty()) {
            return "";
        }
        if (authorizationValue.startsWith(BEARER_PREFIX)) {
            return authorizationValue.substring(7);
        }
        return "";
    }
}
