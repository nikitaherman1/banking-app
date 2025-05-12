package com.backend.bankingapplication.app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BankingUserResponseDTO {

    private Long id;

    private String name;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;
}
