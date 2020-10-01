package com.ecpay.esafebox.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.LimitValue;

@Repository
public interface LimitValueDslRepository extends JpaRepository<LimitValue, Long>, QuerydslPredicateExecutor<LimitValue> {

	LimitValue findOneById(long id);
	
	@Query("select t from LimitValue t where t.id = :limitValueId ")
	Optional<LimitValue> findLimitValueById(@Param("limitValueId") Long limitValueId);

	@Query("select t from LimitValue t where t.limitId = :limitId and t.attributeId = :attributeId")
	Optional<LimitValue> findLimitValueByLimitIdandAttributeId(@Param("limitId") Long limitId, @Param("attributeId") Long attributeId);
}
