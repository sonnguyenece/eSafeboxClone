package com.ecpay.esafebox.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Box;

@Repository
public interface BoxDslRepository extends JpaRepository<Box, Long>, QuerydslPredicateExecutor<Box> {
	List<Box> findByManufactureIdAndStatus(long manufactureId, String boxStatus);

	@Query("select t from Box t " + "where (:manufactureCode is null or t.manufacture.code = :manufactureCode) and "
			+ "(:boxTypeId is null or :boxTypeId = 0l or t.manufacture.boxtypeId = :boxTypeId) and "
			+ "(:boxSerial is null or t.serial = :boxSerial) and "
			+ "(:boxStatus is null or :boxStatus = '0' or t.status = :boxStatus) and "
			+ "(:fromDate IS NULL OR t.created  >= :fromDate ) AND " + "(:toDate IS NULL OR t.created  <= :toDate)")
	Page<Box> getListBox(@Param("manufactureCode") String manufactureCode, @Param("boxTypeId") Long boxTypeId,
			@Param("boxSerial") Long boxSerial, @Param("boxStatus") String boxStatus,
			@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, Pageable pageable);

	@Query(nativeQuery = true, value = "SELECT b.* FROM tb_manufacture m LEFT JOIN tb_boxtype bt ON m.n_boxtype_id = bt.n_id "
			+ "LEFT JOIN tb_box b ON m.n_id = b.n_manufacture_id WHERE bt.s_code = 'STANDARD' "
			+ "AND b.S_STATUS='N' AND ( b.d_expired IS NULL OR b.d_expired > SYSDATE ) AND ROWNUM = 1")
	Optional<Box> findByBoxTypeCode();
	
	@Modifying(clearAutomatically = true)
	@Query("update Box t set t.status = 'Y' where t.id = :boxId")
	void updateStatusById(@Param("boxId") Long boxId);
	
	@Query("SELECT t FROM Box t WHERE t.manufactureId=:manufactureId")
	Page<Box> findByManufactureId(@Param("manufactureId") Long manufactureId, Pageable pageable);
}
