package com.backend.bankingapplication.core.logger.service.impl;

import com.backend.bankingapplication.core.logger.model.impl.TransferLogDTO;
import com.backend.bankingapplication.core.logger.service.AbstractKafkaLogger;
import com.backend.bankingapplication.core.logger.service.KafkaLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransferLoggerImpl extends AbstractKafkaLogger<TransferLogDTO> implements KafkaLogger<TransferLogDTO> {

    @Value("${spring.kafka.topic.transfer-logs}")
    private String topic;

    public TransferLoggerImpl(KafkaTemplate<String, TransferLogDTO> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public String getTopic() {
        return topic;
    }
}
