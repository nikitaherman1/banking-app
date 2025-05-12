package com.backend.bankingapplication.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "transfer_log")
public class TransferLog {

    @Id
    private String idempotencyKey;

    private Long toUserId;

    private Long fromUserId;

    private Boolean success;

    private BigDecimal value;

    private String failureMessage;

    private LocalDateTime createdAt;
}
