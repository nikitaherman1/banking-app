package com.backend.bankingapplication.app.dto.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BankingUserFilterDTO {

    private String name;

    private String email;

    private String phone;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;
}
