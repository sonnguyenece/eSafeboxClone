package com.ecpay.esafebox.service;

import java.util.Arrays;
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

import com.ecpay.entities.ecbox.UomType;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UomTypeDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.UomTypeFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.UomTypeMapper;
import com.ecpay.esafebox.repository.UomtypeDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;

@Service
@Transactional
public class UomtypeDslService {

	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	@Autowired
	private UomtypeDslRepository uomtypeDslRepository;
	@Autowired
	private UomTypeMapper uomTypeMapper;

	public UomTypeDto createUomType(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Saving uomtype {}", logId, data);
		String uomTypeCode = EsafeboxUtils
				.getFieldValueAsString(UomTypeFieldName.UOM_TYPE_CODE.getEsafeboxFieldName().getFieldName(), data)
				.trim().toUpperCase();
		String uomTypeName = EsafeboxUtils
				.getFieldValueAsString(UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), data)
				.trim();
		Long parentId = EsafeboxUtils
				.getFieldValueAsLong(UomTypeFieldName.UOM_TYPE_PARENT.getEsafeboxFieldName().getFieldName(), data);

		if (uomTypeCode.length() < 2) {
			logger.info(
					"[{}] Validating new Uomtype FAIL - Reason: Data for field [{}] is not valid. The length must >= [{}] --> END.",
					logId, UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), 2);
			throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
					EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
							Arrays.asList(UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), "2")));
		}

		// UC003: Uomtype_code is unique
		if (Objects.isNull(parentId))
			parentId = 0L;
		Optional<UomType> obt = uomtypeDslRepository.findUomtypeByCode(uomTypeCode);
		if (obt.isPresent()) {
			logger.info("[{}] Validating new Uomtype FAIL - Reason: {} existed --> END.", logId, uomTypeCode);
			throw new ESafeboxException(EsafeboxErrorCode.UOM_TYPE_EXISTS.getErrorCode(),
					EsafeboxErrorCode.UOM_TYPE_EXISTS.getDescription(Arrays.asList(uomTypeCode)));
		}
		UomType createdUomType = UomType.builder().code(uomTypeCode).name(uomTypeName).parentId(parentId).build();
		return uomTypeMapper.toUomTypeDto(uomtypeDslRepository.save(createdUomType));
	}

	public UomTypeDto updateUomType(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Updating boxtype {}", logId, data);
		Long uomTypeId = EsafeboxUtils
				.getFieldValueAsLong(UomTypeFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
		String uomTypeName = EsafeboxUtils
				.getFieldValueAsString(UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), data)
				.trim();

		// UC3: Boxtype exists
		Optional<UomType> obt = uomtypeDslRepository.findById(uomTypeId);
		if (!obt.isPresent()) {
			logger.info("[{}] Checking existing Boxtype FAIL - Reason: {} not exist --> END.", logId, uomTypeId);
			throw new ESafeboxException(EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomTypeId))));
		}
		UomType updatedUomType = obt.get();

		updatedUomType.setName(uomTypeName);
		return uomTypeMapper.toUomTypeDto(uomtypeDslRepository.save(updatedUomType));
	}

	public PagedData<UomTypeDto> getListUomType(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list box type of {} with paging {}", logId, data, pageable);

		Long uomTypeId = EsafeboxUtils
				.getFieldValueAsLong(UomTypeFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
		String uomTypeCode = EsafeboxUtils
				.getFieldValueAsString(UomTypeFieldName.UOM_TYPE_CODE.getEsafeboxFieldName().getFieldName(), data);
		String uomTypeName = EsafeboxUtils
				.getFieldValueAsString(UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), data);

		// check UC0005
		if (!Objects.isNull(uomTypeCode) && uomTypeCode.length() != 0 && uomTypeCode.length() < 2) {
			logger.info(
					"[{}] Validating new uomTypeCode FAIL - Reason: Data for field [{}] is not valid. The length must >= [{}] --> END.",
					logId, UomTypeFieldName.UOM_TYPE_CODE.getEsafeboxFieldName().getFieldName(), "2");
			throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
					EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
							Arrays.asList(UomTypeFieldName.UOM_TYPE_CODE.getEsafeboxFieldName().getFieldName(), "2")));
		}

		// check UC0006
		if (!Objects.isNull(uomTypeName) && uomTypeName.length() != 0 && uomTypeName.length() < 3) {
			logger.info(
					"[{}] Validating new uomTypeName FAIL - Reason: Data for field [{}] is not valid. The length must >= [{}] --> END.",
					logId, UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), "3");
			throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
					EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
							Arrays.asList(UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName(), "3")));
		}

		Page<UomType> pageUomType = uomtypeDslRepository.getListUomType(uomTypeId, uomTypeCode, uomTypeName, pageable);
		return PagedData.<UomTypeDto>builder().totalElements(pageUomType.getTotalElements())
				.totalPages(pageUomType.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
				.pageSize(pageable.getPageSize()).data(uomTypeMapper.toUomTypeDtos(pageUomType.getContent())).build();
	}
}
