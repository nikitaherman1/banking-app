package com.backend.bankingapplication.controller;

import com.backend.bankingapplication.security.model.AuthorizedUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;

public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser authUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AuthorizedUserDetails principal = new AuthorizedUserDetails(
                AuthorityUtils.createAuthorityList(new ArrayList<>()),
                authUser.userId(),
                null,
                authUser.username()
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                principal.getPassword(),
                principal.getAuthorities()
        );
        context.setAuthentication(authentication);
        return context;
    }
}