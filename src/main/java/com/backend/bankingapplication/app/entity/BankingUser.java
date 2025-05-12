package com.backend.bankingapplication.app.entity;


import com.backend.bankingapplication.core.orm.listener.EntityListener;
import com.backend.bankingapplication.core.orm.entity.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "banking_user")
@EntityListeners(EntityListener.class)
public class BankingUser extends AbstractEntity<Long> {

    public static final String NAME = "name";
    public static final String EMAILS = "emails";
    public static final String PHONES = "phones";
    public static final String DATE_OF_BIRTH = "dateOfBirth";

    private static final String BANKING_USER = "bankingUser";

    private String name;

    private String password;

    private String preferredLogin;

    private LocalDate dateOfBirth;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = BANKING_USER)
    private Account account;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = BANKING_USER)
    private Set<EmailData> emails = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = BANKING_USER)
    private Set<PhoneData> phones = new HashSet<>();
}
