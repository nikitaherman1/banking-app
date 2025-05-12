package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.entity.Account;
import com.backend.bankingapplication.app.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findByUserIdIdWithLock(Long userId) {
        Optional<Account> optionalAccount = accountRepository.findByUserIdIdWithLock(userId);
        if (optionalAccount.isEmpty()) {
            throw new EntityNotFoundException("Account with id " + userId + " not found");
        }
        return optionalAccount.get();
    }
}
