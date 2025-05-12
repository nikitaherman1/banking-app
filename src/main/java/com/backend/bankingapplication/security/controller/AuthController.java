package com.backend.bankingapplication.security.controller;

import com.backend.bankingapplication.security.service.impl.AuthService;
import com.backend.bankingapplication.security.dto.AuthenticationPayloadDTO;
import com.backend.bankingapplication.security.dto.RegistrationPayloadDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    public void registration(@RequestBody RegistrationPayloadDTO payloadDTO, HttpServletResponse response) {
        log.info(
                "A user under the name {} is trying to register in the system, information {}",
                payloadDTO.getName(), payloadDTO
        );
        authService.registration(payloadDTO, response);
    }

    @PostMapping("/login")
    public void authenticate(@RequestBody AuthenticationPayloadDTO authenticationPayloadDTO, HttpServletResponse response) {
        log.info("A user named is attempting to log in, {}", authenticationPayloadDTO.getLogin());
        authService.authenticate(authenticationPayloadDTO, response);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("User from IP addresses {} logs out of the system", request.getRemoteAddr());
        authService.logout(request, response);
    }
}
