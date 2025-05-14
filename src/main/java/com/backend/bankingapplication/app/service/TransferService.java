package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.create.TransferRequestDTO;
import com.backend.bankingapplication.core.exception.DuplicateRequestException;
import com.backend.bankingapplication.security.service.impl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static io.netty.handler.logging.LogLevel.ERROR;
import static io.netty.handler.logging.LogLevel.INFO;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferService {

    private final AuthService authService;
    private final TransferLogService transferLogService;
    private final TransferTransactionService transferTransactionService;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 500),
            retryFor = {ConcurrencyFailureException.class, OptimisticLockingFailureException.class})
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void trySecureTransfer(TransferRequestDTO transferRequestDTO) {
        Long fromUserId = authService.getCurrentUserId();
        log.info(
                "The user with id {} is attempting to transfer funds to the user with the id {}, value {}",
                fromUserId, transferRequestDTO.getToUserId(), transferRequestDTO.getValue()
        );
        try {
            transferTransactionService.secureTransfer(transferRequestDTO, fromUserId);
            log.info(
                    "Transfer of {} funds from user with id {} to user with id {} completed successfully",
                    transferRequestDTO.getValue(), fromUserId, transferRequestDTO.getToUserId()
            );
            transferLogService.saveTransferLog(
                    transferRequestDTO, fromUserId, INFO.name(), null, true
            );
        } catch (DuplicateRequestException exception) {
            log.warn(exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error(
                    "Transfer of {} funds from user with id {} to user with ID {} ended with an error, error message: {}",
                    transferRequestDTO.getValue(), fromUserId, transferRequestDTO.getToUserId(), exception.getMessage()
            );
            transferLogService.saveTransferLog(
                    transferRequestDTO, fromUserId, ERROR.name(), exception.getMessage(), false
            );
            throw exception;
        }
    }
}
