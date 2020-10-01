package com.ecpay.esafebox.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.UomType;

@Repository
public interface UomtypeDslRepository extends JpaRepository<UomType, Long> , QuerydslPredicateExecutor<UomType>{
	
	UomType findOneByCode(String code);
	
	UomType findOneById(long id);

	@Query("select t from UomType t " +
			   "where (:uomTypeId IS NULL OR t.id = :uomTypeId) and " + 
			   "(:uomTypeCode IS NULL OR lower(t.code) like lower(concat('%', :uomTypeCode,'%'))) and " + 
			   "(:uomTypeName IS NULL OR lower(t.name) like lower(concat('%', :uomTypeName,'%')))")
		Page<UomType> getListUomType(@Param("uomTypeId") Long uomTypeId, @Param("uomTypeCode") String uomTypeCode, @Param("uomTypeName") String uomTypeName, Pageable pageable);
	
	@Query("select t from UomType t where t.code = :uomTypeCode ")
	Optional<UomType> findUomtypeByCode(@Param("uomTypeCode") String uomTypeCode);
}
