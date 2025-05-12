package com.backend.bankingapplication.app.repository;

import com.backend.bankingapplication.app.entity.BankingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BankingUserRepository extends JpaRepository<BankingUser, Long>, JpaSpecificationExecutor<BankingUser> {

    BankingUser findByPhonesPhoneEquals(String phone);

    BankingUser findByEmailsEmailEquals(String email);
}
