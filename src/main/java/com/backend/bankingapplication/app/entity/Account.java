package com.backend.bankingapplication.app.entity;

import com.backend.bankingapplication.core.orm.listener.EntityListener;
import com.backend.bankingapplication.core.orm.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "account")
@EntityListeners(EntityListener.class)
public class Account extends AbstractEntity<Long> {

    private static final String USER_ID = "user_id";

    private BigDecimal balance;

    private BigDecimal initialBalance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID)
    private BankingUser bankingUser;
}
