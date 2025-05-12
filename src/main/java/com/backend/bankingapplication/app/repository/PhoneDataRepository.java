package com.backend.bankingapplication.app.repository;

import com.backend.bankingapplication.app.entity.PhoneData;
import com.backend.bankingapplication.core.orm.repository.OwnerShipRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDataRepository extends OwnerShipRepository<PhoneData, Long> {

    boolean existsByPhone(String phone);

    @Query("SELECT COUNT(e) FROM PhoneData e WHERE e.bankingUser.id = :userId")
    Long countPhonesByUserId(@Param("userId") Long userId);
}
