package com.ecpay.esafebox.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Limit;

@Repository
public interface LimitDslRepository extends JpaRepository<Limit, Long>, QuerydslPredicateExecutor<Limit> {

	Limit findOneByCode(String code);

	Limit findOneById(long id);

	@Query("select t from Limit t "
			+ "where (:limitId IS NULL OR t.id = :limitId) and "
			+ "(:limitCode IS NULL OR lower(t.code) like lower(concat('%', :limitCode,'%'))) and "
			+ "(:limitName IS NULL OR lower(t.name) like lower(concat('%', :limitName,'%'))) and "
			+ "(:limitType IS NULL OR t.type = :limitType) and "
			+ "(:boxTypeId IS NULL OR t.boxTypeId = :boxTypeId)")
	Page<Limit> getListBoxTypeLimit(@Param("limitId") Long limitId, @Param("limitCode") String limitCode,
			@Param("limitName") String limitName, @Param("limitType") Long limitType, @Param("boxTypeId") Long boxTypeId,
			Pageable pageable);

	@Query("select t from Limit t where t.code = :limitCode ")
	Optional<Limit> findLimitByCode(@Param("limitCode") String limitCode);
}
