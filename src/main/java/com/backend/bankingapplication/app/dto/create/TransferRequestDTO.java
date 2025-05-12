package com.backend.bankingapplication.app.dto.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequestDTO {

    @NotNull(message = "Recipient ID cannot be empty")
    private Long toUserId;

    @DecimalMin(value = "0.00", message = "The transfer amount cannot be negative")
    private BigDecimal value;

    @NotBlank(message = "Idempotency key missing")
    private String idempotencyKey;
}
