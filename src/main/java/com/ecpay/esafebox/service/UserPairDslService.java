package com.ecpay.esafebox.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecpay.entities.ecbox.UserPair;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UserPairDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.UserPairFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.UserPairMapper;
import com.ecpay.esafebox.repository.UserPairDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;

@Service
@Transactional
public class UserPairDslService {

	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	@Autowired
	private UserPairDslRepository userPairDslRepository;
	@Autowired
	private UserPairMapper userPairMapper;

	public UserPairDto addUserPair(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] add user pair {}", logId, data);
		Long userPairUserFrom = EsafeboxUtils
				.getFieldValueAsLong(UserPairFieldName.USER_PAIR_USER_FROM.getEsafeboxFieldName().getFieldName(), data);
		Long userPairUserTo = EsafeboxUtils
				.getFieldValueAsLong(UserPairFieldName.USER_PAIR_USER_TO.getEsafeboxFieldName().getFieldName(), data);
		// UC0004: UserFrom+UserTo is unique
		Optional<UserPair> obt = userPairDslRepository.findUserPairByUserFromAndUserTo(userPairUserFrom,
				userPairUserTo);
		if (obt.isPresent()) {
			logger.info(
					"[{}] Validating new user pair FAIL - Reason: Setting userTo [{}] for userFrom [{}] is existed. --> END.",
					logId, userPairUserTo, userPairUserFrom);
			throw new ESafeboxException(EsafeboxErrorCode.USER_PAIR_IS_EXISTED.getErrorCode(),
					EsafeboxErrorCode.USER_PAIR_IS_EXISTED.getDescription(
							Arrays.asList(String.valueOf(userPairUserTo), String.valueOf(userPairUserFrom))));
		}
		UserPair createdUserPair = UserPair.builder().userFrom(userPairUserFrom).userTo(userPairUserTo).status("Y")
				.build();
		return userPairMapper.toUserPairDto(userPairDslRepository.save(createdUserPair));
	}

	public List<UserPairDto> unpairUser(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Updating boxtype {}", logId, data);
		Long userPairUserFrom = EsafeboxUtils
				.getFieldValueAsLong(UserPairFieldName.USER_PAIR_USER_FROM.getEsafeboxFieldName().getFieldName(), data);
		List<Long> userPairUserTos = EsafeboxUtils.getFieldValueAsArrayOfLong(
				UserPairFieldName.USER_PAIR_USER_TO_LIST.getEsafeboxFieldName().getFieldName(), data);

		// UC0003:
		if (!Objects.isNull(userPairUserTos)) {
			for (Long userPairUserTo : userPairUserTos) {
				Optional<UserPair> obt = userPairDslRepository.findUserPairByUserFromAndUserTo(userPairUserFrom,
						userPairUserTo);
				if (!obt.isPresent()) {
					logger.info(
							"[{}] Validating unpair user FAIL - Reason: Setting userTo {} for userFrom {} does not exist --> END.",
							logId, userPairUserTo, userPairUserFrom);
					throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_CODE_EXISTS.getErrorCode(),
							EsafeboxErrorCode.ATTRIBUTE_CODE_EXISTS
									.getDescription(Arrays.asList(String.valueOf(userPairUserTo))));
				}
			}
		} else {
			throw new ESafeboxException(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID.getErrorCode(),
					EsafeboxErrorCode.SORTS_PARAM_IS_INVALID.getDescription(Arrays
							.asList(UserPairFieldName.USER_PAIR_USER_TO_LIST.getEsafeboxFieldName().getFieldName())));
		}

		List<UserPair> updatedUserPair = userPairDslRepository.getListUnpairUser(userPairUserFrom, userPairUserTos);

		for (UserPair userPair : updatedUserPair) {
			userPair.setStatus("N");
		}
		;
		return userPairMapper.toUserPairDtos(userPairDslRepository.saveAll(updatedUserPair));
	}

	public PagedData<UserPairDto> getListPairUser(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list user pair of {} with paging {}", logId, data, pageable);
		try {
			Long userPairUserFrom = EsafeboxUtils.getFieldValueAsLong(
					UserPairFieldName.USER_PAIR_USER_FROM.getEsafeboxFieldName().getFieldName(), data);
			Long userPairUserTo = EsafeboxUtils.getFieldValueAsLong(
					UserPairFieldName.USER_PAIR_USER_TO.getEsafeboxFieldName().getFieldName(), data);
			String userPairStatus = EsafeboxUtils.getFieldValueAsString(
					UserPairFieldName.USER_PAIR_STATUS.getEsafeboxFieldName().getFieldName(), data);
			Page<UserPair> userPairs = userPairDslRepository.getListUserPair(userPairUserFrom, userPairUserTo,
					userPairStatus, pageable);
			return PagedData.<UserPairDto>builder().totalElements(userPairs.getTotalElements())
					.totalPages(userPairs.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
					.pageSize(pageable.getPageSize()).data(userPairMapper.toUserPairDtos(userPairs.getContent()))
					.build();
		} catch (Exception e) {
			logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
	}
}
