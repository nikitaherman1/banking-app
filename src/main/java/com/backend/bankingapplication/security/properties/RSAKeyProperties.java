package com.backend.bankingapplication.security.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "rsa")
public class RSAKeyProperties {
    private String publicKeyLocation;
    private String privateKeyLocation;
}