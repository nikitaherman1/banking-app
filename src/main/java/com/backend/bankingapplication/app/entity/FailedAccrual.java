package com.backend.bankingapplication.app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "failed_accrual")
public class FailedAccrual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean processed;

    private Integer retryCount;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime lastRetryAt;
}
