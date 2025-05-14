package com.backend.bankingapplication.core.logger.service;

import com.backend.bankingapplication.core.logger.model.Loggable;

public interface KafkaLogger<T extends Loggable> {

    void logEvent(T log);
}
