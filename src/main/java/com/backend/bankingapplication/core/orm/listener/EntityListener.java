package com.backend.bankingapplication.core.orm.listener;

import com.backend.bankingapplication.core.configuration.sql.SnowFlakeIdGenerator;
import com.backend.bankingapplication.core.orm.entity.Identifiable;
import jakarta.persistence.PrePersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityListener {

    private final SnowFlakeIdGenerator idGenerator;

    @PrePersist
    public void assignId(Identifiable<Long> entity) {
        if (null == entity.getId()) {
            entity.setId(idGenerator.nextId());
        }
    }
}
