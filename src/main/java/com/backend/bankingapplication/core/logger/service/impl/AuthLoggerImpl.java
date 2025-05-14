package com.backend.bankingapplication.core.logger.service.impl;

import com.backend.bankingapplication.core.logger.model.impl.AuthLogDTO;
import com.backend.bankingapplication.core.logger.service.AbstractKafkaLogger;
import com.backend.bankingapplication.core.logger.service.KafkaLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthLoggerImpl extends AbstractKafkaLogger<AuthLogDTO> implements KafkaLogger<AuthLogDTO> {

    @Value("${spring.kafka.topic.auth-logs}")
    private String topic;

    public AuthLoggerImpl(KafkaTemplate<String, AuthLogDTO> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public String getTopic() {
        return topic;
    }
}
