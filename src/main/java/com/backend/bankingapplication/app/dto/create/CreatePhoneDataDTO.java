package com.backend.bankingapplication.app.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePhoneDataDTO {

    @NotBlank(message = "The phone cannot be empty")
    @Size(min = 13, max = 13, message = "The number length must be 13 characters")
    @Pattern(regexp = "^\\+375\\d{9}$", message = "The number format is set incorrectly")
    private String phone;
}
