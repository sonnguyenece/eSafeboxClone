package com.ecpay.esafebox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.Manufacture;

@Repository
public interface ManufactureDslRepository extends JpaRepository<Manufacture, Long> , QuerydslPredicateExecutor<Manufacture>{
	
	Manufacture findOneByCode(String code);
	
	Manufacture findOneById(long id);

}
