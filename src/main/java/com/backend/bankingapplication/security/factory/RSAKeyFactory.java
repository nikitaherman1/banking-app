package com.backend.bankingapplication.security.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class RSAKeyFactory {

    private static final String RSA_ALGORITHM = "RSA";

    public Key getKey(String keyLocation, Class<? extends RSAKey> keyClazz) {
        String key = getKeyFromFile(keyLocation);
        key = cleanKey(key);

        KeyFactory keyFactory = getKeyFactory();
        if (null == keyFactory) {
            log.error("KeyFactory not initialized");
            throw new RuntimeException("KeyFactory not initialized");
        }
        byte[] decodedKeyInBytes = Base64.getDecoder().decode(key);

        try {
            return generateKey(keyFactory, decodedKeyInBytes, keyClazz);
        } catch (InvalidKeySpecException exception) {
            log.error("Throw invalid key spec exception with error message: {}", exception.getMessage());
            throw new RuntimeException("Invalid key spec", exception);
        }
    }

    private String cleanKey(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll(" ", "")
                .replaceAll("\\s+", "");
    }

    private String getKeyFromFile(String keyLocation) {
        ClassPathResource resource = new ClassPathResource(keyLocation);

        try(InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes());
        } catch (IOException exception) {
            log.error("IO exception when read file in location {}, error message {}", keyLocation, exception.getMessage());
            throw new RuntimeException("IO exception when read file in location " + keyLocation, exception);
        }
    }

    public Key generateKey(KeyFactory keyFactory, byte[] decodedKeyInBytes, Class<? extends RSAKey> keySpecClazz) throws InvalidKeySpecException {
        if (keySpecClazz.isAssignableFrom(RSAPrivateKey.class)) {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decodedKeyInBytes);
            log.info("Let's start generating a private rsa key");
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } else {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(decodedKeyInBytes);
            log.info("Let's start generating a public rsa key");
            return keyFactory.generatePublic(x509EncodedKeySpec);
        }
    }

    public KeyFactory getKeyFactory() {
        try {
            return KeyFactory.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException exception) {
            log.error("Throw no such algorithm: " + RSA_ALGORITHM, exception);
            return null;
        }
    }
}
