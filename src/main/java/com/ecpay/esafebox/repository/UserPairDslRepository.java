package com.ecpay.esafebox.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecpay.entities.ecbox.UserPair;

@Repository
public interface UserPairDslRepository extends JpaRepository<UserPair, Long>, QuerydslPredicateExecutor<UserPair> {

	UserPair findOneById(long id);

	@Query("select t from UserPair t " + "where (:userPairUserFrom = 0L OR t.userFrom = :userPairUserFrom) AND "
			+ "(:userPairUserTo = 0L OR t.userTo = :userPairUserTo) AND "
			+ "(:userPairStatus = '0' OR (:userPairStatus = 'Y' AND t.status = 'Y') OR (:userPairStatus = 'N' AND t.status = 'N'))")
	Page<UserPair> getListUserPair(@Param("userPairUserFrom") Long userPairUserFrom,
			@Param("userPairUserTo") Long userPairUserTo, @Param("userPairStatus") String userPairStatus,
			Pageable pageable);

	@Query("select t from UserPair t " + "where t.userFrom = :userPairUserFrom and " + "t.userTo IN :userToList")
	List<UserPair> getListUnpairUser(@Param("userPairUserFrom") Long userPairUserFrom,
			@Param("userToList") List<Long> userToList);

	@Query("select t from UserPair t where t.userFrom = :userPairUserFrom AND t.userTo = :userPairUserTo")
	Optional<UserPair> findUserPairByUserFromAndUserTo(@Param("userPairUserFrom") Long userPairUserFrom,
			@Param("userPairUserTo") Long userPairUserTo);
}
