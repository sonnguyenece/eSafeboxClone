package com.ecpay.esafebox.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Uom;

@Repository
public interface UomDslRepository extends JpaRepository<Uom, Long>, QuerydslPredicateExecutor<Uom> {

	Uom findOneById(long id);

	@Query("select t from Uom t " + "where (:uomId IS NULL OR t.id = :uomId) and "
			+ "(:uomTypeId IS NULL OR t.uomTypeId = :uomTypeId) and "
			+ "(:uomAbbreviation IS NULL OR lower(t.abbreviation) like lower(concat('%', :uomAbbreviation,'%'))) and "
			+ "(:uomName IS NULL OR lower(t.name) like lower(concat('%', :uomName,'%')))")
	Page<Uom> getListUom(@Param("uomId") Long uomId, @Param("uomTypeId") Long uomTypeId,
			@Param("uomAbbreviation") String uomAbbreviation, @Param("uomName") String uomName, Pageable pageable);

	@Query("select t from Uom t where t.abbreviation = :uomAbbreviation ")
	Optional<Uom> findUomByAbbreviation(@Param("uomAbbreviation") String uomAbbreviation);
}
