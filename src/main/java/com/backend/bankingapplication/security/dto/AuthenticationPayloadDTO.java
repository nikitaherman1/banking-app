package com.backend.bankingapplication.security.dto;

import lombok.Getter;
import lombok.Setter;

// TODO validation
@Getter
@Setter
public class AuthenticationPayloadDTO {

    private String login;

    private String password;
}
