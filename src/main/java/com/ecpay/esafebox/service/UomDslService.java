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

import com.ecpay.entities.ecbox.Uom;
import com.ecpay.entities.ecbox.UomType;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UomDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.UomFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.UomMapper;
import com.ecpay.esafebox.repository.UomDslRepository;
import com.ecpay.esafebox.repository.UomtypeDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class UomDslService {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	@Autowired
	private UomDslRepository uomDslRepository;
	@Autowired
	private UomtypeDslRepository uomTypeDslRepository;
	@Autowired
	private UomMapper uomMapper;

	public UomDto createUom(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Saving uom {}", logId, data);
		Long uomTypeId = EsafeboxUtils
				.getFieldValueAsLong(UomFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
		String uomAbbreviation = EsafeboxUtils
				.getFieldValueAsString(UomFieldName.UOM_ABBREVIATION.getEsafeboxFieldName().getFieldName(), data).trim()
				.toUpperCase();
		String uomName = EsafeboxUtils
				.getFieldValueAsString(UomFieldName.UOM_NAME.getEsafeboxFieldName().getFieldName(), data).trim();

		// UC004: uomTypeId is unique
		Optional<UomType> obt = uomTypeDslRepository.findById(uomTypeId);
		if (!obt.isPresent()) {
			logger.info("[{}] Validating new Uom FAIL - Reason: {} existed --> END.", logId, uomTypeId);
			throw new ESafeboxException(EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomTypeId))));
		}

		// UC003: uomAbbreviation is unique
		Optional<Uom> obt2 = uomDslRepository.findUomByAbbreviation(uomAbbreviation);
		if (obt2.isPresent()) {
			logger.info("[{}] Validating new Uom FAIL - Reason: {} existed --> END.", logId, uomAbbreviation);
			throw new ESafeboxException(EsafeboxErrorCode.UOM_ABREVIATION_EXISTS.getErrorCode(),
					EsafeboxErrorCode.UOM_ABREVIATION_EXISTS
							.getDescription(Arrays.asList(String.valueOf(uomAbbreviation))));
		}

		Uom createdUom = Uom.builder().uomTypeId(uomTypeId).abbreviation(uomAbbreviation).name(uomName).build();
		return uomMapper.toUomDto(uomDslRepository.save(createdUom));
	}

	public UomDto updateUom(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Updating boxtype {}", logId, data);
		Long uomId = EsafeboxUtils.getFieldValueAsLong(UomFieldName.UOM_ID.getEsafeboxFieldName().getFieldName(), data);
		Long uomTypeId = EsafeboxUtils
				.getFieldValueAsLong(UomFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
		String uomAbbreviation = EsafeboxUtils
				.getFieldValueAsString(UomFieldName.UOM_ABBREVIATION.getEsafeboxFieldName().getFieldName(), data).trim()
				.toUpperCase();
		String uomName = EsafeboxUtils
				.getFieldValueAsString(UomFieldName.UOM_NAME.getEsafeboxFieldName().getFieldName(), data).trim();

		// UC003: uomAbbreviation is unique
		Optional<Uom> obt2 = uomDslRepository.findUomByAbbreviation(uomAbbreviation);
		if (obt2.isPresent()) {
			logger.info("[{}] Validating new Uom FAIL - Reason: {} existed --> END.", logId, uomAbbreviation);
			throw new ESafeboxException(EsafeboxErrorCode.UOM_ABREVIATION_EXISTS.getErrorCode(),
					EsafeboxErrorCode.UOM_ABREVIATION_EXISTS
							.getDescription(Arrays.asList(String.valueOf(uomAbbreviation))));
		}

		// UC003: UOM does not exist
		Optional<Uom> obt = uomDslRepository.findById(uomId);
		if (!obt.isPresent()) {
			logger.info("[{}] Checking existing UOM FAIL - Reason: {} does not exist --> END.", logId, uomId);
			throw new ESafeboxException(EsafeboxErrorCode.UOM_ABREVIATION_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.UOM_ABREVIATION_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomId))));
		}
		Uom updatedUom = obt.get();

		// UC004: checking existing of uomTypeId
		if (!Objects.isNull(uomTypeId) && !updatedUom.getUomTypeId().equals(uomTypeId)) {
			Optional<UomType> os = uomTypeDslRepository.findById(uomTypeId);
			if (!os.isPresent()) {
				logger.info("[{}] Validating new uomTypeId FAIL - Reason: {} not existed --> END.", logId, uomTypeId);
				throw new ESafeboxException(EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomTypeId))));
			}

			updatedUom.setUomTypeId(uomTypeId);
			updatedUom.setUomType(os.get());
		}

		updatedUom.setAbbreviation(uomAbbreviation);
		updatedUom.setName(uomName);
		return uomMapper.toUomDto(uomDslRepository.save(updatedUom));
	}

	public PagedData<UomDto> getListUom(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list UOM of {} with paging {}", logId, data, pageable);
		Long uomId = EsafeboxUtils.getFieldValueAsLong(UomFieldName.UOM_ID.getEsafeboxFieldName().getFieldName(), data);
		Long uomTypeId = EsafeboxUtils
				.getFieldValueAsLong(UomFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
		String uomAbbreviation = EsafeboxUtils
				.getFieldValueAsString(UomFieldName.UOM_ABBREVIATION.getEsafeboxFieldName().getFieldName(), data);
		String uomName = EsafeboxUtils
				.getFieldValueAsString(UomFieldName.UOM_NAME.getEsafeboxFieldName().getFieldName(), data);
		// UC0004: uomId does not exist
		if (!Objects.isNull(uomId) && !StringUtils.isEmpty(uomId)) {
			Optional<Uom> obt = uomDslRepository.findById(uomId);
			if (!obt.isPresent()) {
				logger.info("[{}] Checking existing UOM FAIL - Reason: {} does not exist --> END.", logId, uomId);
				throw new ESafeboxException(EsafeboxErrorCode.UOM_ID_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.UOM_ID_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomId))));
			}
		}

		// UC0005: uomTypeId does not exist
		if (!Objects.isNull(uomTypeId) && !StringUtils.isEmpty(uomTypeId)) {
			Optional<UomType> obut = uomTypeDslRepository.findById(uomTypeId);
			if (!obut.isPresent()) {
				logger.info("[{}] Checking existing UOM FAIL - Reason: {} does not exist --> END.", logId, uomId);
				throw new ESafeboxException(EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.UOM_TYPE_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomTypeId))));
			}
		}

		// UC0007: uomName does not exist
		if (!Objects.isNull(uomName) && !StringUtils.isEmpty(uomName) && uomName.length() < 3) {
			logger.info("[{}] Checking existing UOM FAIL - Reason: {} uomName length must >= 3 --> END.", logId, uomId);
			throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
					EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
							Arrays.asList(UomFieldName.UOM_NAME.getEsafeboxFieldName().getFieldName(), "3")));
		}
		Page<Uom> pageUom = uomDslRepository.getListUom(uomId, uomTypeId, uomAbbreviation, uomName, pageable);
		return PagedData.<UomDto>builder().totalElements(pageUom.getTotalElements()).totalPages(pageUom.getTotalPages())
				.pageNumber(pageable.getPageNumber() + 1).pageSize(pageable.getPageSize())
				.data(uomMapper.toUomDtos(pageUom.getContent())).build();
	}
}