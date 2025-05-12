package com.backend.bankingapplication.core.task;

import com.backend.bankingapplication.app.entity.FailedAccrual;
import com.backend.bankingapplication.app.repository.AccountRepository;
import com.backend.bankingapplication.app.repository.FailedAccrualRepository;
import com.backend.bankingapplication.app.service.BankingUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserIncreaseBalanceTask {

    private final BankingUserService userService;
    private final AccountRepository accountRepository;
    private final FailedAccrualRepository failedAccrualRepository;

    @Transactional
    @Scheduled(cron = "1/30 * * * * *")
    public void increaseBalance() {
        log.info("We start accruing interest on the initial deposit...");

        try {
            accountRepository.updateAccountBalance();
            log.info("The accruals were successful");

            userService.evictUserProfilesCache();

            retryFailedAccruals();
        } catch (Exception exception) {
            log.error("Error while crediting deposit, message {}", exception.getMessage(), exception);
            createAndSaveFailedAccrual(exception.getMessage());
        }
    }

    private void createAndSaveFailedAccrual(String errorMessage) {
        FailedAccrual failedAccrual = new FailedAccrual();
        failedAccrual.setRetryCount(0);
        failedAccrual.setProcessed(false);
        failedAccrual.setErrorMessage(errorMessage);
        failedAccrual.setCreatedAt(LocalDateTime.now());

        failedAccrualRepository.save(failedAccrual);
    }

    private void retryFailedAccruals() {
        List<FailedAccrual> failedAccruals = failedAccrualRepository.findByProcessedFalse();

        for (FailedAccrual accrual : failedAccruals) {
            try {
                accountRepository.updateAccountBalance();
                accrual.setProcessed(true);
                accrual.setLastRetryAt(LocalDateTime.now());
                log.info("The attempt to update the accrual was successful. accrual id={}", accrual.getId());
                userService.evictUserProfilesCache();
            } catch (Exception exception) {
                accrual.setLastRetryAt(LocalDateTime.now());
                accrual.setRetryCount(accrual.getRetryCount() + 1);
                log.warn("Retry failed for accrual with id={}, attempt count #{}", accrual.getId(), accrual.getRetryCount());
            }
            failedAccrualRepository.save(accrual);
        }
    }
}
