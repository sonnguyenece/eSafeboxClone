package com.ecpay.esafebox.repository;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Transaction;
import com.ecpay.entities.ecbox.enumeration.TransactionDataType;

@Repository
public interface TransactionDslRepository
		extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {

	@Query("select t from Transaction t " + "where (:sender = 0L or t.sender = :sender) and "
			+ "(:receiver = 0L or t.receiver = :receiver) and "
			+ "(:transactionType IS NULL or t.transactionDataType = :transactionType) and "
			+ "t.transactionTime  >= :fromDate AND t.transactionTime  <= :toDate")
	Page<Transaction> getListBoxTransaction(@Param("sender") Long sender, @Param("receiver") Long receiver,
			@Param("transactionType") TransactionDataType transactionType, @Param("fromDate") Timestamp fromDate,
			@Param("toDate") Timestamp toDate, Pageable pageable);
}
