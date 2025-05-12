package com.backend.bankingapplication.app.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class BankingUserProfileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 134543535255L;

    private String name;

    private BigDecimal balance;

    private Set<EmailDataResponseDTO> emails;

    private Set<PhoneDataResponseDTO> phones;
}
