package com.backend.bankingapplication.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizedUserDetailsService extends UserDetailsService {

    UserDetails loadByUserId(Long userId);
}
