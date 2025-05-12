package com.backend.bankingapplication.core.orm.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class VersionedAbstractEntity<T extends Serializable> extends AbstractEntity<T> {

    @Version
    private Integer version;
}
