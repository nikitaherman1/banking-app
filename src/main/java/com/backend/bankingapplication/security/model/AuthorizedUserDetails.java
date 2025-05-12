package com.backend.bankingapplication.security.model;

import com.backend.bankingapplication.app.entity.BankingUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthorizedUserDetails implements UserDetails {

    private final Long userId;
    private final String login;
    private final String password;

    public AuthorizedUserDetails(BankingUser bankingUser) {
        this.login = bankingUser.getPreferredLogin();
        this.userId = bankingUser.getId();
        this.password = bankingUser.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.login;
    }
}
