package com.backend.bankingapplication.security.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

// TODO validation
@Getter
@Setter
@ToString
public class RegistrationPayloadDTO {

    private String name;

    private String password;

    private String preferredLogin;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    private BigDecimal balance;

    private Set<String> emails;

    private Set<String> phones;
}
