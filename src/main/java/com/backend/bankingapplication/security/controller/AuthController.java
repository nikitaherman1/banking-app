package com.backend.bankingapplication.security.controller;

import com.backend.bankingapplication.security.dto.AuthenticationPayloadDTO;
import com.backend.bankingapplication.security.dto.RegistrationPayloadDTO;
import com.backend.bankingapplication.security.service.impl.AuthLogService;
import com.backend.bankingapplication.security.service.impl.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.backend.bankingapplication.core.constants.SecurityAction.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthLogService authLogService;

    @PostMapping("/registration")
    public void registration(
            @RequestBody RegistrationPayloadDTO payloadDTO, HttpServletRequest request, HttpServletResponse response
    ) {
        log.info(
                "A user under the name {} is trying to register in the system, information {}",
                payloadDTO.getName(), payloadDTO
        );
        authLogService.saveAuthLog(REGISTRATION, request.getRemoteAddr());
        authService.registration(payloadDTO, response);
    }

    @PostMapping("/login")
    public void authenticate(
            @RequestBody AuthenticationPayloadDTO authenticationPayloadDTO,
            HttpServletRequest request, HttpServletResponse response)
    {
        log.info("A user named is attempting to log in, {}", authenticationPayloadDTO.getLogin());
        authLogService.saveAuthLog(LOGIN, request.getRemoteAddr());
        authService.authenticate(authenticationPayloadDTO, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("User from IP addresses {} logs out of the system", request.getRemoteAddr());
        authLogService.saveAuthLog(LOGOUT, request.getRemoteAddr());
        authService.logout(request, response);
    }
}
