package com.backend.bankingapplication.app.service;

import com.backend.bankingapplication.app.dto.create.TransferRequestDTO;
import com.backend.bankingapplication.core.logger.model.impl.TransferLogDTO;
import com.backend.bankingapplication.core.logger.service.KafkaLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferLogService {

    private final KafkaLogger<TransferLogDTO> transferLogger;

    public void saveTransferLog(
            TransferRequestDTO requestDTO, Long fromUserId, String level, String failureMessage, boolean success
    ) {
        TransferLogDTO transferLogDTO = new TransferLogDTO();
        transferLogDTO.setLevel(level);
        transferLogDTO.setSuccess(success);
        transferLogDTO.setFromUserId(fromUserId);
        transferLogDTO.setValue(requestDTO.getValue());
        transferLogDTO.setCreatedAt(LocalDateTime.now());
        transferLogDTO.setFailureMessage(failureMessage);
        transferLogDTO.setToUserId(requestDTO.getToUserId());
        transferLogDTO.setIdempotencyKey(requestDTO.getIdempotencyKey());

        log.info("Send transfer log to kafka: {}", transferLogDTO);
        transferLogger.logEvent(transferLogDTO);
    }
}
