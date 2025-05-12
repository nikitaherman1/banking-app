package com.backend.bankingapplication.core.orm.repository;

import com.backend.bankingapplication.core.orm.entity.Ownable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.io.Serializable;
import java.util.Optional;

@NoRepositoryBean
public interface OwnerShipRepository<T extends Ownable<I>, I extends Serializable> extends JpaRepository<T, I> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.bankingUser.id = :ownerId")
    Optional<T> findByIdAndOwnerId(
            @Param("id") Long id,
            @Param("ownerId") Long ownerId
    );
}
