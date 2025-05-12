package com.backend.bankingapplication.security.provider;

import com.backend.bankingapplication.security.factory.RSAKeyFactory;
import com.backend.bankingapplication.security.properties.RSAKeyProperties;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Slf4j
@Getter
@Component
public class RSAKeyProvider {

    private RSAPublicKey rsaPublicKey;
    private RSAPrivateKey rsaPrivateKey;

    private final RSAKeyFactory rsaKeyFactory;
    private final RSAKeyProperties rsaKeyProperties;

    public RSAKeyProvider(RSAKeyFactory rsaKeyFactory, RSAKeyProperties rsaKeyProperties) {
        this.rsaKeyFactory = rsaKeyFactory;
        this.rsaKeyProperties = rsaKeyProperties;
    }

    @PostConstruct
    public void init() {
        log.info("Let's start initialization a rsa keys");

        this.rsaPublicKey = (RSAPublicKey) rsaKeyFactory.getKey(rsaKeyProperties.getPublicKeyLocation(), RSAPublicKey.class);
        this.rsaPrivateKey = (RSAPrivateKey) rsaKeyFactory.getKey(rsaKeyProperties.getPrivateKeyLocation(), RSAPrivateKey.class);
    }
}
