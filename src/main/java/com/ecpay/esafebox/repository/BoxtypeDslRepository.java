package com.ecpay.esafebox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Boxtype;

@Repository
public interface BoxtypeDslRepository extends JpaRepository<Boxtype, Long>, QuerydslPredicateExecutor<Boxtype> {

	Boxtype findOneByCode(String code);

	Boxtype findOneById(long id);

	@Query("select t from Boxtype t "
			+ "where (:boxTypeCode IS NULL OR lower(t.code) like lower(concat('%', :boxTypeCode,'%'))) and "
			+ "(:boxTypeName IS NULL OR lower(t.name) like lower(concat('%', :boxTypeName,'%'))) and "
			+ "(:boxTypePrice is null or t.price = :boxTypePrice) and "
			+ "(:boxTypeSale is null or t.sale = :boxTypeSale) and " + "(:setId IS NULL OR t.setId  = :setId )")
	Page<Boxtype> getListBoxType(@Param("boxTypeCode") String boxTypeCode, @Param("boxTypeName") String boxTypeName,
			@Param("boxTypePrice") Long boxTypePrice, @Param("boxTypeSale") Long boxTypeSale,
			@Param("setId") Long setId, Pageable pageable);

	@Query("select t from Boxtype t "
			+ "where (:boxTypeCode IS NULL OR lower(t.code) like lower(concat('%', :boxTypeCode,'%'))) and "
			+ "(:boxTypePrice is null or t.price = :boxTypePrice) and "
			+ "(:boxTypeSale is null or t.sale = :boxTypeSale) and " + "(:setId IS NULL OR t.setId  = :setId )")
	List<Boxtype> getListBoxType2(@Param("boxTypeCode") String boxTypeCode, @Param("boxTypePrice") Long boxTypePrice,
			@Param("boxTypeSale") Long boxTypeSale, @Param("setId") Long setId, Sort sort);

	@Query("select t from Boxtype t where t.code = :boxTypeCode ")
	Optional<Boxtype> findBoxtypeByCode(@Param("boxTypeCode") String boxTypeCode);
}
