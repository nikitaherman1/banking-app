package com.backend.bankingapplication.core.logger.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface Loggable extends Serializable {

    String getLevel();

    LocalDateTime getCreatedAt();
}
