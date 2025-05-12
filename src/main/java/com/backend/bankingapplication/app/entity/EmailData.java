package com.backend.bankingapplication.app.entity;

import com.backend.bankingapplication.core.orm.listener.EntityListener;
import com.backend.bankingapplication.core.orm.entity.Ownable;
import com.backend.bankingapplication.core.orm.entity.VersionedAbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "email_data")
@EntityListeners(EntityListener.class)
public class EmailData extends VersionedAbstractEntity<Long> implements Ownable<Long> {

    public static final String EMAIL = "email";

    private static final String USER_ID = "user_id";

    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = USER_ID)
    private BankingUser bankingUser;
}
