package com.backend.bankingapplication.app.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class CreateEmailDataDTO {

    @NonNull
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email format is incorrectly specified")
    @Size(min = 5, max = 200, message = "Email length must be between 5 and 200 characters")
    private String email;
}
