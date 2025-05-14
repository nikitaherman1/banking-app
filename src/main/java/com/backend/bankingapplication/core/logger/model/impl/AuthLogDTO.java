package com.backend.bankingapplication.core.logger.model.impl;

import com.backend.bankingapplication.core.constants.SecurityAction;
import com.backend.bankingapplication.core.logger.model.Loggable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
public class AuthLogDTO implements Loggable {

    @Serial
    private static final long serialVersionUID = 6534565645L;

    private String level;

    private String clientIpAddress;

    private SecurityAction action;

    private LocalDateTime createdAt;

    @Override
    public String getLevel() {
        return level;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
