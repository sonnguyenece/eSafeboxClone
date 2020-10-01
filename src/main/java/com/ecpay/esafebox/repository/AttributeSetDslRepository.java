package com.ecpay.esafebox.repository;

import com.ecpay.entities.ecbox.Attribute;
import com.ecpay.entities.ecbox.AttributeSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AttributeSetDslRepository extends JpaRepository<AttributeSet, Long>, QuerydslPredicateExecutor<Attribute> {
    Optional<AttributeSet> findAttributeSetByAttributeId(Long attributeId);
}
