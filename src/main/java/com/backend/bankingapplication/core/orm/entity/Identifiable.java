package com.backend.bankingapplication.core.orm.entity;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {

    T getId();

    void setId(T id);
}