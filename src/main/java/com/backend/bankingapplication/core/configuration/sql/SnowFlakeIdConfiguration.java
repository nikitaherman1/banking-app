package com.backend.bankingapplication.core.configuration.sql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnowFlakeIdConfiguration {

    @Value("${snow-flake.node-id}")
    private long nodeId;

    @Bean
    public SnowFlakeIdGenerator configureSnowFlakeIdGenerator() {
        return new SnowFlakeIdGenerator(nodeId);
    }
}
