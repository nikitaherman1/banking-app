package com.backend.bankingapplication.app.repository;

import com.backend.bankingapplication.app.entity.FailedAccrual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedAccrualRepository extends JpaRepository<FailedAccrual, Long> {

    List<FailedAccrual> findByProcessedFalse();
}
