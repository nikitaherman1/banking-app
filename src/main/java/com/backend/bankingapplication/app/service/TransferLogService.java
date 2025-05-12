package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.create.TransferRequestDTO;
import com.backend.bankingapplication.app.entity.TransferLog;
import com.backend.bankingapplication.app.repository.TransferLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferLogService {

    private final TransferLogRepository transferLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAndSaveLog(TransferRequestDTO requestDTO, Long fromUserId, Boolean success, String failureMessage) {
        TransferLog transferLog = new TransferLog();
        transferLog.setSuccess(success);
        transferLog.setFromUserId(fromUserId);
        transferLog.setValue(requestDTO.getValue());
        transferLog.setFailureMessage(failureMessage);
        transferLog.setCreatedAt(LocalDateTime.now());
        transferLog.setToUserId(requestDTO.getToUserId());
        transferLog.setIdempotencyKey(requestDTO.getIdempotencyKey());

        log.info("Saving transfer log: {}", transferLog);
        transferLogRepository.save(transferLog);
    }
}
