package com.backend.bankingapplication.security.service.impl;

import com.backend.bankingapplication.core.constants.KeyPrefix;
import com.backend.bankingapplication.security.model.AuthorizedUserDetails;
import com.backend.bankingapplication.security.service.AuthorizedUserDetailsService;
import com.backend.bankingapplication.app.entity.BankingUser;
import com.backend.bankingapplication.app.repository.BankingUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizedUserDetailsServiceImpl implements AuthorizedUserDetailsService {

    private final BankingUserRepository userRepository;

    @Override
    @Cacheable(cacheNames = KeyPrefix.USER_DETAILS_PREFIX, key = "#userId")
    public UserDetails loadByUserId(Long userId) {
        Optional<BankingUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new AuthorizedUserDetails(optionalUser.get());
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        BankingUser bankingUser = userRepository.findByEmailsEmailEquals(login);
        if (null == bankingUser) {
            bankingUser = userRepository.findByPhonesPhoneEquals(login);
        }
        if (null == bankingUser) {
            throw new UsernameNotFoundException(login);
        }
        return new AuthorizedUserDetails(bankingUser);
    }
}
