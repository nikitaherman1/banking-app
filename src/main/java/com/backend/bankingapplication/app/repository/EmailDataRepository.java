package com.backend.bankingapplication.app.repository;

import com.backend.bankingapplication.app.entity.EmailData;
import com.backend.bankingapplication.core.orm.repository.OwnerShipRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDataRepository extends OwnerShipRepository<EmailData, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(e) FROM EmailData e WHERE e.bankingUser.id = :userId")
    Long countEmailsByUserId(@Param("userId") Long userId);
}
