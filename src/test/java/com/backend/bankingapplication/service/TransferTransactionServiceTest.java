package com.backend.bankingapplication.service;

import com.backend.bankingapplication.app.dto.create.TransferRequestDTO;
import com.backend.bankingapplication.app.entity.Account;
import com.backend.bankingapplication.app.repository.AccountRepository;
import com.backend.bankingapplication.app.repository.TransferLogRepository;
import com.backend.bankingapplication.app.service.AccountService;
import com.backend.bankingapplication.app.service.TransferTransactionService;
import com.backend.bankingapplication.core.exception.BadRequestDataException;
import com.backend.bankingapplication.core.exception.DuplicateRequestException;
import com.backend.bankingapplication.core.exception.InsufficientFundsException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TransferTransactionServiceTest {

    @InjectMocks
    private TransferTransactionService transferTransactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferLogRepository transferLogRepository;

    private final Long toUserId = 1L;
    private final Long fromUserId = 2L;

    @Test
    void shouldThrowBadRequestDataException_whenTransferringToSelf() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(fromUserId);
        transferRequestDTO.setValue(BigDecimal.valueOf(100));

        assertThrows(
                BadRequestDataException.class,
                () -> transferTransactionService.secureTransfer(transferRequestDTO, fromUserId)
        );
    }

    @Test
    void shouldThrowInsufficientFundsException_whenBalanceIsNotEnough() {
        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(toUserId);
        transferRequestDTO.setValue(BigDecimal.valueOf(1000));

        Account sourceAccount = new Account();
        sourceAccount.setBalance(BigDecimal.valueOf(500));

        when(accountService.findByUserIdIdWithLock(fromUserId)).thenReturn(sourceAccount);

        assertThrows(
                InsufficientFundsException.class,
                () -> transferTransactionService.secureTransfer(transferRequestDTO, fromUserId)
        );
    }

    @Test
    void shouldThrowDuplicateRequestException_whenIdempotencyKeyIsAlreadyUsed() {
        String idempotencyKey = "duplicate-key";

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(toUserId);
        transferRequestDTO.setValue(BigDecimal.valueOf(100));
        transferRequestDTO.setIdempotencyKey(idempotencyKey);

        when(transferLogRepository.existsByIdempotencyKey(idempotencyKey)).thenReturn(true);

        assertThrows(
                DuplicateRequestException.class,
                () -> transferTransactionService.secureTransfer(transferRequestDTO, fromUserId)
        );
    }

    @Test
    void shouldSuccessfullyTransferMoney_whenRequestIsValid() {
        String idempotencyKey = "unique-key";

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(toUserId);
        transferRequestDTO.setValue(BigDecimal.valueOf(100));
        transferRequestDTO.setIdempotencyKey(idempotencyKey);

        Account sourceAccount = new Account();
        sourceAccount.setBalance(BigDecimal.valueOf(500));

        Account targetAccount = new Account();
        targetAccount.setBalance(BigDecimal.valueOf(200));

        when(accountService.findByUserIdIdWithLock(fromUserId))
                .thenReturn(sourceAccount);
        when(accountService.findByUserIdIdWithLock(toUserId))
                .thenReturn(targetAccount);
        when(transferLogRepository.existsByIdempotencyKey(idempotencyKey))
                .thenReturn(false);

        transferTransactionService.secureTransfer(transferRequestDTO, fromUserId);

        assertEquals(BigDecimal.valueOf(400), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(300), targetAccount.getBalance());
        verify(accountRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveAccountsInTransaction_whenTransferIsSuccessful() {
        String idempotencyKey = "unique-key";

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(toUserId);
        transferRequestDTO.setValue(BigDecimal.valueOf(100));
        transferRequestDTO.setIdempotencyKey(idempotencyKey);

        Account sourceAccount = new Account();
        sourceAccount.setBalance(BigDecimal.valueOf(500));

        Account targetAccount = new Account();
        targetAccount.setBalance(BigDecimal.valueOf(200));

        when(accountService.findByUserIdIdWithLock(fromUserId))
                .thenReturn(sourceAccount);
        when(accountService.findByUserIdIdWithLock(toUserId))
                .thenReturn(targetAccount);
        when(transferLogRepository.existsByIdempotencyKey(idempotencyKey))
                .thenReturn(false);

        transferTransactionService.secureTransfer(transferRequestDTO, fromUserId);

        verify(accountRepository, times(1))
                .saveAll(Arrays.asList(sourceAccount, targetAccount));
    }

    @Test
    void shouldThrowDatabaseException_whenAccountSaveFails() {
        String idempotencyKey = "unique-key";

        TransferRequestDTO transferRequestDTO = new TransferRequestDTO();
        transferRequestDTO.setToUserId(toUserId);
        transferRequestDTO.setValue(BigDecimal.valueOf(100));
        transferRequestDTO.setIdempotencyKey(idempotencyKey);

        Account sourceAccount = new Account();
        sourceAccount.setBalance(BigDecimal.valueOf(500));

        Account targetAccount = new Account();
        targetAccount.setBalance(BigDecimal.valueOf(200));

        when(accountService.findByUserIdIdWithLock(fromUserId))
                .thenReturn(sourceAccount);
        when(accountService.findByUserIdIdWithLock(toUserId))
                .thenReturn(targetAccount);
        when(transferLogRepository.existsByIdempotencyKey(idempotencyKey))
                .thenReturn(false);
        doThrow(new DataAccessException("Database error") {})
                .when(accountRepository).saveAll(anyList());

        assertThrows(
                DataAccessException.class,
                () -> transferTransactionService.secureTransfer(transferRequestDTO, fromUserId)
        );
    }

    @Test
    void shouldHandleConcurrentTransactions_whenUpdatingSameAccount() throws InterruptedException {
        TransferRequestDTO firstTransferRequest = new TransferRequestDTO();
        firstTransferRequest.setToUserId(2L);
        firstTransferRequest.setValue(BigDecimal.valueOf(100));
        firstTransferRequest.setIdempotencyKey("unique-key-1");

        TransferRequestDTO secondTransferRequest = new TransferRequestDTO();
        secondTransferRequest.setToUserId(1L);
        secondTransferRequest.setValue(BigDecimal.valueOf(100));
        secondTransferRequest.setIdempotencyKey("unique-key-2");

        Account firstSourceAccount = new Account();
        firstSourceAccount.setBalance(BigDecimal.valueOf(500));

        Account firstTargetAccount = new Account();
        firstTargetAccount.setBalance(BigDecimal.valueOf(200));

        Account secondSourceAccount = new Account();
        secondSourceAccount.setBalance(BigDecimal.valueOf(500));

        Account secondTargetAccount = new Account();
        secondTargetAccount.setBalance(BigDecimal.valueOf(200));

        when(accountService.findByUserIdIdWithLock(1L))
                .thenReturn(firstSourceAccount)
                .thenReturn(secondSourceAccount);
        when(accountService.findByUserIdIdWithLock(2L))
                .thenReturn(firstTargetAccount)
                .thenReturn(secondTargetAccount);
        when(transferLogRepository.existsByIdempotencyKey(anyString()))
                .thenReturn(false);

        Thread firstThread = new Thread(() -> {
            try {
                transferTransactionService.secureTransfer(firstTransferRequest, 1L);
            } catch (Exception exception) {
                log.error("Exception in thread1: {}", exception.getMessage());
            }
        });
        Thread secondThread = new Thread(() -> {
            try {
                transferTransactionService.secureTransfer(secondTransferRequest, 2L);
            } catch (Exception exception) {
                log.error("Exception in thread2: {}", exception.getMessage());
            }
        });

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        verify(accountRepository, times(2)).saveAll(anyList());
    }
}
