package com.backend.bankingapplication.app.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateEmailDataDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is incorrectly specified")
    @Size(min = 5, max = 200, message = "Email length must be between 5 and 200 characters")
    private String email;
}
