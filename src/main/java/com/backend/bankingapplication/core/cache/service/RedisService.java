package com.backend.bankingapplication.core.cache.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private ValueOperations<String, String> valueOperations;

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    private void init() {
        valueOperations = redisTemplate.opsForValue();
    }

    public void setAndExpire(String key, String value, Integer expiration, TimeUnit timeUnit) {
        valueOperations.set(key, value);
        redisTemplate.expire(key, expiration, timeUnit);
    }

    public void setAndExpire(String key, String token, Integer expiration) {
        setAndExpire(key, token, expiration, TimeUnit.MILLISECONDS);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public String get(String key) {
        return valueOperations.get(key);
    }
}
