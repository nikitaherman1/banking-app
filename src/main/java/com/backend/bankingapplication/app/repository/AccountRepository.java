package com.backend.bankingapplication.app.repository;

import com.backend.bankingapplication.app.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Modifying
    @Query("UPDATE Account a " +
            "SET a.balance = CASE " +
            "   WHEN a.balance * 1.10 > a.initialBalance * 2.07 THEN a.initialBalance * 2.07 " +
            "   ELSE a.balance * 1.10 " +
            "END " +
            "WHERE a.balance < a.initialBalance * 2.07")
    void updateAccountBalance();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.bankingUser.id = :userId")
    Optional<Account> findByUserIdIdWithLock(@Param("userId") Long userId);
}
