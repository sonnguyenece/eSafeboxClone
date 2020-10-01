package com.ecpay.esafebox.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Value;

@Repository
public interface ValueDslRepository extends JpaRepository<Value, Long> , QuerydslPredicateExecutor<Value>{
	
	Value findOneById(long id);

	@Query("select t from Value t where t.boxTypeId = :boxTypeId AND t.attributeId = :attributeId")
	Optional<Value> findByBoxtypeIdandAttrId(@Param("boxTypeId") Long boxTypeId, @Param("attributeId") Long attributeId);
	
	@Query("select t from Value t where t.boxTypeId = :boxTypeId")
	List<Value> findByBoxtypeId(@Param("boxTypeId") Long boxTypeId);
	
	@Modifying(clearAutomatically = true)
	@Query("update Value t set t.genericValue = :genericValue where t.boxTypeId = :boxTypeId AND t.attributeId = :attributeId")
	void updateByBoxtypeIdandAttrId(@Param("boxTypeId") Long boxTypeId, @Param("attributeId") Long attributeId, @Param("genericValue") String genericValue);
}
