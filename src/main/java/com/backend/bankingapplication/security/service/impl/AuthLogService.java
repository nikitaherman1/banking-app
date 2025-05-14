package com.backend.bankingapplication.security.service.impl;

import com.backend.bankingapplication.core.constants.SecurityAction;
import com.backend.bankingapplication.core.logger.model.impl.AuthLogDTO;
import com.backend.bankingapplication.core.logger.service.KafkaLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static io.netty.handler.logging.LogLevel.INFO;

@Service
@RequiredArgsConstructor
public class AuthLogService {

    private final KafkaLogger<AuthLogDTO> authLogger;

    public void saveAuthLog(SecurityAction action, String clientIpAddress) {
        AuthLogDTO authLogDTO = new AuthLogDTO();
        authLogDTO.setAction(action);
        authLogDTO.setLevel(INFO.name());
        authLogDTO.setCreatedAt(LocalDateTime.now());
        authLogDTO.setClientIpAddress(clientIpAddress);
        authLogger.logEvent(authLogDTO);
    }
}
