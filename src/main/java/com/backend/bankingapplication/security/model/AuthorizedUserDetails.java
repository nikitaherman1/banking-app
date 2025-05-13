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
    private List<GrantedAuthority> grantedAuthorities;

    public AuthorizedUserDetails(BankingUser bankingUser) {
        this.login = bankingUser.getPreferredLogin();
        this.userId = bankingUser.getId();
        this.password = bankingUser.getPassword();
    }

    public AuthorizedUserDetails(List<GrantedAuthority> grantedAuthorities, Long userId, String password, String login) {
        this.grantedAuthorities = grantedAuthorities;
        this.userId = userId;
        this.password = password;
        this.login = login;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
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
