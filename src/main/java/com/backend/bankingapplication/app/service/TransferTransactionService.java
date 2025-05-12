package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.create.TransferRequestDTO;
import com.backend.bankingapplication.app.entity.Account;
import com.backend.bankingapplication.app.repository.AccountRepository;
import com.backend.bankingapplication.app.repository.TransferLogRepository;
import com.backend.bankingapplication.core.exception.BadRequestDataException;
import com.backend.bankingapplication.core.exception.DuplicateRequestException;
import com.backend.bankingapplication.core.exception.InsufficientFundsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferTransactionService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransferLogRepository transferLogRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void secureTransfer(TransferRequestDTO transferRequestDTO, Long fromUserId) {
        validateTransferRequest(transferRequestDTO, fromUserId);
        validateIdempotencyKey(transferRequestDTO);
        log.info("Transfer request by user {} validated successfully", fromUserId);

        BigDecimal value = transferRequestDTO.getValue();

        Account source = getSourceAccountAndCheckBalance(fromUserId, value);
        Account target = accountService.findByUserIdIdWithLock(transferRequestDTO.getToUserId());

        transferMoney(source, target, value);
    }

    private void transferMoney(Account sourceAccount, Account targetAccount, BigDecimal value) {
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(value));
        targetAccount.setBalance(targetAccount.getBalance().add(value));

        accountRepository.saveAll(Arrays.asList(sourceAccount, targetAccount));
    }

    private void validateIdempotencyKey(TransferRequestDTO transferRequestDTO) {
        String idempotencyKey = transferRequestDTO.getIdempotencyKey();
        if (transferLogRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.warn("Duplicate request detected with idempotency key: {}", idempotencyKey);
            throw new DuplicateRequestException("This operation was already executed");
        }
    }

    private Account getSourceAccountAndCheckBalance(Long fromUserId, BigDecimal value) {
        Account sourceAccount = accountService.findByUserIdIdWithLock(fromUserId);
        if (sourceAccount.getBalance().compareTo(value) < 0) {
            log.warn("User with id {} does not have enough funds", fromUserId);
            throw new InsufficientFundsException("Insufficient funds");
        }
        return sourceAccount;
    }

    private void validateTransferRequest(TransferRequestDTO request, Long fromUserId) {
        Long toUserId = request.getToUserId();
        if (fromUserId.equals(toUserId)) {
            log.warn("The user with the id {} is trying to transfer funds to himself", fromUserId);
            throw new BadRequestDataException("Cannot transfer money to yourself");
        }
        log.info("Transfer request validated successfully");
    }
}
