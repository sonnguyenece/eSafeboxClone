package com.ecpay.esafebox.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.TbSet;

@Repository
public interface SetDslRepository extends JpaRepository<TbSet, Long>, QuerydslPredicateExecutor<TbSet> {
    TbSet findOneById(long id);

    @Query("select t from TbSet t where (:setId IS NULL OR t.id = :setId) " +
            "and(:setCode IS NULL OR lower(t.code) like lower(concat('%', :setCode,'%'))) " +
            "and (:setName IS NULL OR lower(t.name) like lower(concat('%', :setName,'%')))")
    Page<TbSet> getListSet(@Param("setId") Long setId,@Param("setCode") String setCode, @Param("setName") String setName, Pageable pageable);

    @Query("select t from TbSet t where t.code = :setCode ")
    Optional<TbSet> findSetByCode(@Param("setCode") String setCode);
}
