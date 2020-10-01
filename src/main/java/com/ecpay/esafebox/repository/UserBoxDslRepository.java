package com.ecpay.esafebox.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Box;
import com.ecpay.entities.ecbox.UserBox;

@Repository
public interface UserBoxDslRepository  extends JpaRepository<UserBox, Long> , QuerydslPredicateExecutor<UserBox>{
	@Query("select t from UserBox t " +
			   "where (:userId is null or t.userId = :userId) and " + 
			   "(:boxTypeId is null or :boxTypeId = 0l or t.box.manufacture.boxtypeId = :boxTypeId) and " + 
			   "(:boxSerial is null or t.box.serial = :boxSerial) and " + 
			   "(:fromDate IS NULL OR t.regiDate  >= :fromDate ) AND " +
			   "(:toDate IS NULL OR t.regiDate  <= :toDate)")
		Page<UserBox> getListUserBox(@Param("userId") Long userId, @Param("boxTypeId") Long boxTypeId, @Param("boxSerial") Long boxSerial, 
				@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate, Pageable pageable);
	
	@Query("SELECT b FROM UserBox ub, Box b WHERE ub.box = b AND ub.userId = :userId AND ub.terminalId = :terminalId")
	List<Box> findBoxByUserIdAndTerminalId(@Param("userId") Long userId, @Param("terminalId") String terminalId);
	
	@Query("SELECT ub FROM UserBox ub WHERE ub.userId = :userId AND ub.boxId = :boxId AND ub.terminalId = :terminalId")
	Optional<UserBox> findByUnique(@Param("userId") Long userId,@Param("boxId") Long boxId, @Param("terminalId") String terminalId);
	
	@Query("SELECT ub FROM UserBox ub WHERE ub.box.serial IN :serialList and ub.box.status = 'Y'")
	List<UserBox> findByListBoxSerial(@Param("serialList") List<Long> boxSerialList);
	
	@Query("SELECT t FROM UserBox t WHERE t.userId = :userId AND t.box.status = 'Y'")
	List<UserBox> getListActiveUserBoxByUserId(@Param("userId") Long userId);
	
	@Query("SELECT ub FROM UserBox ub WHERE ub.userId = :userId AND ub.boxId = :boxId ")
	Optional<UserBox> findByUserIdAndBoxId(@Param("userId") Long userId, @Param("boxId") Long boxId);
}
