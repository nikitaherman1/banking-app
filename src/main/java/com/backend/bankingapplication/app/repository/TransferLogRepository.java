package com.backend.bankingapplication.app.repository;

import com.backend.bankingapplication.app.entity.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferLogRepository extends JpaRepository<TransferLog, Long> {

    boolean existsByIdempotencyKey(String idempotencyKey);
}
