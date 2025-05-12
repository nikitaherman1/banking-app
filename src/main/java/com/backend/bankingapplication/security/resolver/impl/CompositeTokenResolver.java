package com.backend.bankingapplication.security.resolver.impl;

import com.backend.bankingapplication.security.resolver.TokenResolver;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompositeTokenResolver implements TokenResolver{

    private final List<TokenResolver> tokenResolvers;

    @Override
    public String resolve(HttpServletRequest request) {
        for (TokenResolver tokenResolver : tokenResolvers) {
            String token = tokenResolver.resolve(request);
            if (!StringUtil.isNullOrEmpty(token)) {
                return token;
            }
        }
        throw new AuthenticationServiceException("Token cookie or header is empty");
    }
}
