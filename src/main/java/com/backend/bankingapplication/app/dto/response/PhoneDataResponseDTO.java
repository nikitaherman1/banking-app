package com.backend.bankingapplication.app.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class PhoneDataResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234234454353L;

    private String phone;
}
