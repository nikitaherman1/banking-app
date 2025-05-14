package com.backend.bankingapplication.core.logger.model.impl;

import com.backend.bankingapplication.core.logger.model.Loggable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransferLogDTO implements Loggable {

    @Serial
    private static final long serialVersionUID = 1645336456L;

    private String idempotencyKey;

    private String level;

    private Long toUserId;

    private Long fromUserId;

    private Boolean success;

    private BigDecimal value;

    private String failureMessage;

    private LocalDateTime createdAt;

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getLevel() {
        return level;
    }
}
