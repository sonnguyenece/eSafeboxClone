package com.ecpay.esafebox.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ecpay.entities.ecbox.Box;
import com.ecpay.entities.ecbox.UserBox;
import com.ecpay.esafebox.algorithm.EllipticCurve;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UserBoxDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UserBoxFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.UserBoxMapper;
import com.ecpay.esafebox.repository.BoxDslRepository;
import com.ecpay.esafebox.repository.UserBoxDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.TimeUtils;

@Service
@Transactional
public class UserBoxDslService {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);

	@Autowired
	UserBoxDslRepository userBoxDslRepository;

	@Autowired
	BoxDslRepository boxDslRepository;

	@Autowired
	UserBoxMapper userBoxMapper;

	public PagedData<UserBoxDto> getListUserBox(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list userbox of {} with paging {}", logId, data, pageable);
		try {
			Long userId = EsafeboxUtils
					.getFieldValueAsLong(UserBoxFieldName.USER_ID.getEsafeboxFieldName().getFieldName(), data);
			Long boxTypeId = EsafeboxUtils
					.getFieldValueAsLong(BoxFieldName.BOX_TYPE.getEsafeboxFieldName().getFieldName(), data);
			if (Objects.isNull(boxTypeId) || StringUtils.isEmpty(boxTypeId)) {
				boxTypeId = Long.valueOf(0l);
			}
			Long boxSerial = EsafeboxUtils
					.getFieldValueAsLong(BoxFieldName.BOX_SERIAL.getEsafeboxFieldName().getFieldName(), data);
			String fDate = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName(), data);
			String tDate = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.TO_DATE.getEsafeboxFieldName().getFieldName(), data);
			LocalDateTime fromDate = TimeUtils.convertString2LocalDate(fDate).atStartOfDay();
			LocalDateTime toDate = TimeUtils.convertString2LocalDate(tDate).atTime(LocalTime.MAX);
			Page<UserBox> pageBox = userBoxDslRepository.getListUserBox(userId, boxTypeId, boxSerial, fromDate, toDate,
					pageable);
			return PagedData.<UserBoxDto>builder().totalElements(pageBox.getTotalElements())
					.totalPages(pageBox.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
					.pageSize(pageable.getPageSize()).data(userBoxMapper.toUserBoxDtos(pageBox.getContent())).build();
		} catch (Exception e) {
			logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}
	}

	public List<UserBoxDto> activeUserBox(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Activate userBox {}", logId, data);
		Long userId = EsafeboxUtils.getFieldValueAsLong(UserBoxFieldName.USER_ID.getEsafeboxFieldName().getFieldName(),
				data);
		String terminalId = EsafeboxUtils
				.getFieldValueAsString(UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName().getFieldName(), data);
		String terminalInfo = EsafeboxUtils
				.getFieldValueAsString(UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName().getFieldName(), data);

		// check UC007 throw S108
		UserBox ub;
		List<UserBox> boxes = userBoxDslRepository.getListActiveUserBoxByUserId(userId);
		if (!(Objects.isNull(boxes) || boxes.isEmpty())) {
			//userBox is existed
			return userBoxMapper.toUserBoxDtos(boxes);
		} else {
			//get randomly box
			Optional<Box> ob = boxDslRepository.findByBoxTypeCode();
			if (!ob.isPresent()) {
				//box is not found
				logger.info("[{}] Validating for activate userBox FAIL - Reason: Box does not exist --> END.");
				throw new ESafeboxException(EsafeboxErrorCode.BOX_DOES_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.BOX_DOES_NOT_EXISTS.getDescription());
			} else {
				//box exists, update status
				Box b = ob.get();
				b.setStatus("Y");
				b.setActived(LocalDateTime.now());
				//create userBox
				ub = UserBox.builder()
						.userId(userId)
						.boxId(b.getId())
						.boxKey(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase())
						.terminalId(terminalId).terminalInfo(terminalInfo)
						.regiDate(LocalDateTime.now()).build();
				boxDslRepository.saveAndFlush(b);
				return userBoxMapper.toUserBoxDtos(Arrays.asList(userBoxDslRepository.saveAndFlush(ub)));
			}
		}
	}
	
	public UserBoxDto updateBoxKey(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] update userBox {}", logId, data);
		Long userId = EsafeboxUtils.getFieldValueAsLong(UserBoxFieldName.USER_ID.getEsafeboxFieldName().getFieldName(),
				data);
		Long boxId = EsafeboxUtils.getFieldValueAsLong(UserBoxFieldName.BOX_ID.getEsafeboxFieldName().getFieldName(),
				data);
		//validate box
		Optional<Box> box = boxDslRepository.findById(boxId);
		if (!box.isPresent()) {
			logger.info("[{}] Validating for upate userBox FAIL - Reason: Box does not exist --> END.");
			throw new ESafeboxException(EsafeboxErrorCode.BOX_ID_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.BOX_ID_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(boxId))));
		}
		// find userBox by userId vs boxId
		Optional<UserBox> oub = userBoxDslRepository.findByUserIdAndBoxId(userId, boxId);
		if (!oub.isPresent()) {
			logger.info("[{}] Validating for upate userBox FAIL - Reason: Box does not exist --> END.");
			throw new ESafeboxException(EsafeboxErrorCode.USER_BOX_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.USER_BOX_NOT_EXISTS.getDescription());
		}
		UserBox userBox = oub.get();
		
		String alias = EsafeboxUtils.getFieldValueAsString(UserBoxFieldName.USERBOX_ALIAS.getEsafeboxFieldName().getFieldName(),
				data);
		if (Objects.nonNull(alias) && !StringUtils.isEmpty(alias.trim())) {
			userBox.setAlias(alias);
		} else {
			if (Objects.isNull(userBox.getAlias())) {
				logger.info("[{}] Validating for upate userBox FAIL - Reason: Param [alias] missed --> END.");
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(UserBoxFieldName.USERBOX_ALIAS.getEsafeboxFieldName().getFieldName()));
			}
		}
		
		String algorithm = EsafeboxUtils.getFieldValueAsString(UserBoxFieldName.USERBOX_ALGORITHM.getEsafeboxFieldName().getFieldName(),
				data);
		if (Objects.nonNull(algorithm) && !StringUtils.isEmpty(algorithm.trim())) {
			try {
				EllipticCurve.getByCurveName(algorithm);
				userBox.setAlgorithm(algorithm);
			} catch (Exception e) {
				logger.info("[{}] Validating for upate userBox FAIL - Reason: Param [algorithm] is Invalid --> END.");
				throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID.getErrorCode(),
						EsafeboxErrorCode.DATA_IS_INVALID.getDescription(Arrays.asList(UserBoxFieldName.USERBOX_ALGORITHM.getEsafeboxFieldName().getFieldName(), algorithm)));
			}
		} else {
			if (Objects.isNull(userBox.getAlgorithm())) {
				logger.info("[{}] Validating for upate userBox FAIL - Reason: Param [algorithm] missed --> END.");
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(UserBoxFieldName.USERBOX_ALGORITHM.getEsafeboxFieldName().getFieldName()));
			}
		}
		
		String publicKey = EsafeboxUtils.getFieldValueAsString(UserBoxFieldName.USERBOX_KP.getEsafeboxFieldName().getFieldName(),
				data);
		if (Objects.nonNull(publicKey) && !StringUtils.isEmpty(publicKey.trim())) {
			try {
				userBox.setBoxKp(Hex.encodeHexString(Base64.getDecoder().decode(publicKey))); 
			} catch (Exception e) {
				throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID.getErrorCode(),
						EsafeboxErrorCode.DATA_IS_INVALID.getDescription(Arrays.asList(UserBoxFieldName.USERBOX_KP.getEsafeboxFieldName().getFieldName(), publicKey)));
			}
		} else {
			if (Objects.isNull(userBox.getBoxKp())) {
				logger.info("[{}] Validating for upate userBox FAIL - Reason: Param [publicKey] missed --> END.");
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(UserBoxFieldName.USERBOX_KP.getEsafeboxFieldName().getFieldName()));
			}
		}
		
		//validate passed --> persist to DB
		UserBoxDto updatedUserBox = userBoxMapper.toUserBoxDto(userBoxDslRepository.saveAndFlush(userBox));
		updatedUserBox.setKeyBox(null);//remove boxkey in response data
		return updatedUserBox;
	}
}
