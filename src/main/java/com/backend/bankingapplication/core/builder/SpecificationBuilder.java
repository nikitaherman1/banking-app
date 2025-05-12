package com.backend.bankingapplication.core.builder;

import com.backend.bankingapplication.app.entity.BankingUser;
import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.BiFunction;

import static java.util.Objects.isNull;

public class SpecificationBuilder {

    private Specification<BankingUser> socksSpecification;

    public <Y> SpecificationBuilder withJoin(
            String relationName, BiFunction<Join<BankingUser, Y>, CriteriaBuilder, Predicate> predicateFunction
    ) {
        Assert.hasText("Relation name cannot be empty", relationName);

        Specification<BankingUser> spec = (root, query, criteriaBuilder) -> {
            Join<BankingUser, Y> join = root.join(relationName, JoinType.LEFT);
            return predicateFunction.apply(join, criteriaBuilder);
        };

        return createOrUpdateSpecification(spec);
    }

    public SpecificationBuilder andGreaterThan(LocalDate value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<BankingUser> specification = (root, query, criteriaBuilder) ->
                isNull(value)
                        ? null
                        : criteriaBuilder.greaterThan(root.get(fieldName), value);

        return createOrUpdateSpecification(specification);
    }

    public <T extends Serializable> SpecificationBuilder andNotEquals(T value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<BankingUser> specification = (root, query, criteriaBuilder) ->
                isNull(value)
                        ? null
                        : criteriaBuilder.notEqual(root.get(fieldName), value);

        return createOrUpdateSpecification(specification);
    }

    public SpecificationBuilder andLike(String value, String fieldName) {
        Assert.hasText("Field name cannot be empty", fieldName);

        Specification<BankingUser> specification = (root, query, criteriaBuilder) ->
                isNull(value) ?
                        null
                        : criteriaBuilder.like(root.get(fieldName), value + "%");

        return createOrUpdateSpecification(specification);
    }

    private SpecificationBuilder createOrUpdateSpecification(Specification<BankingUser> specification) {
        if (this.socksSpecification == null) {
            this.socksSpecification = Specification.where(specification);
        } else {
            this.socksSpecification = socksSpecification.and(specification);
        }

        return this;
    }

    public Specification<BankingUser> build() {
        Specification<BankingUser> temporarySpecification = this.socksSpecification;
        this.socksSpecification = null;

        return temporarySpecification;
    }
}