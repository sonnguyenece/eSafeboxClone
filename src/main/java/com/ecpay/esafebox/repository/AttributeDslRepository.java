package com.ecpay.esafebox.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Attribute;

@Repository
public interface AttributeDslRepository extends JpaRepository<Attribute, Long>, QuerydslPredicateExecutor<Attribute> {

	Attribute findOneByCode(String code);

	Attribute findOneById(long id);

	@Query("select t from Attribute t where t.id = :attributeId ")
	Optional<Attribute> findAttributeById(@Param("attributeId") Long attributeId);

    Optional<Attribute> findAttributeByCode(String attributeCode);

    @Query("SELECT t FROM com.ecpay.entities.ecbox.Attribute t "
            + "WHERE (:attributeId IS NULL OR t.id = :attributeId) "
            + "AND(:attributeCode IS NULL OR lower(t.code) LIKE LOWER(CONCAT('%', :attributeCode,'%'))) "
            + "AND (:attributeName IS NULL OR lower(t.name) LIKE LOWER(CONCAT('%', :attributeName,'%'))) "
            + "AND (:uomId IS NULL OR t.uomId  >= :uomId )"
    )
    Page<Attribute> getListAttribute(@Param("attributeId") Long attributeId, @Param("attributeCode") String attributeCode,
                                     @Param("attributeName") String attributeName, @Param("uomId") Long uomId, Pageable pageable);
}
