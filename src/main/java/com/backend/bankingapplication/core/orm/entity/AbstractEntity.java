package com.backend.bankingapplication.core.orm.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Identifiable<T> {

    public static final String ID = "id";

    @Id
    protected T id;
}
