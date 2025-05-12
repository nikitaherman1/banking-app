package com.backend.bankingapplication.app.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class EmailDataResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 16575634745L;

    private String email;
}
