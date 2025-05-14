package com.backend.bankingapplication.core.logger.service;

import com.backend.bankingapplication.core.logger.model.Loggable;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public abstract class AbstractKafkaLogger<T extends Loggable> implements KafkaLogger<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;

    @Override
    public void logEvent(T log) {
        kafkaTemplate.send(getTopic(), log);
    }

    public abstract String getTopic();
}
