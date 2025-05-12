package com.backend.bankingapplication.security.filter;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class SecurityFilterConfiguration {

    private final List<String> permittedUrls = List.of(
            "/auth/registration",
            "/auth/login",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**"
    );
}