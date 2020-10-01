package com.ecpay.esafebox.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecpay.entities.ecbox.Boxtype;
import com.ecpay.entities.ecbox.TbSet;
import com.ecpay.entities.ecbox.Value;
import com.ecpay.esafebox.dto.BoxTypeDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxTypeFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.BoxTypeMapper;
import com.ecpay.esafebox.mapper.BoxTypeValueMapper;
import com.ecpay.esafebox.repository.BoxtypeDslRepository;
import com.ecpay.esafebox.repository.SetDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;

@Service
@Transactional
public class BoxtypeDslService {

	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	@Autowired
	private BoxtypeDslRepository boxtypeDslRepository;

	@Autowired
	private SetDslRepository setDslRepository;

	@Autowired
	private BoxTypeMapper boxTypeMapper;

	@Autowired
	private BoxTypeValueMapper boxTypeValueMapper;

	public Boxtype getUniqueBoxtype(Long id) {
		return boxtypeDslRepository.findOneById(id);
	}

	public BoxTypeDto createBoxType(Long logId, Map<String, Object> data) throws ESafeboxException {
//		logger.info("[{}] Saving boxtype {}", logId, data);
		String boxTypeCode = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName().getFieldName(), data)
				.trim().toUpperCase();
		String boxTypeName = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName().getFieldName(), data)
				.trim();
		Long boxTypePrice = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_PRICE.getEsafeboxFieldName().getFieldName(), data);
		Long boxTypeSale = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_SALE.getEsafeboxFieldName().getFieldName(), data);
		Long setId = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_SET.getEsafeboxFieldName().getFieldName(), data);
		String boxTypeIcon = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_ICON.getEsafeboxFieldName().getFieldName(), data);
		logger.info(
				"[{}] [Saving boxtype] boxTypeCode:[{}], boxTypeName:[{}], boxTypePrice:[{}], boxTypeSale:[{}], setId:[{}]",
				logId, boxTypeCode, boxTypeName, boxTypePrice, boxTypeSale, setId);

		// UC3: Boxtype_code is unique
		Optional<Boxtype> obt = boxtypeDslRepository.findBoxtypeByCode(boxTypeCode);
		if (obt.isPresent()) {
			logger.info("[{}] Validating new Boxtype FAIL - Reason: {} existed --> END.", logId, boxTypeCode);
			throw new ESafeboxException(EsafeboxErrorCode.BOXTYPE_CODE_EXISTS.getErrorCode(),
					EsafeboxErrorCode.BOXTYPE_CODE_EXISTS.getDescription(Arrays.asList(boxTypeCode)));
		}
		// UC004: checking existing of setId
		Optional<TbSet> os = setDslRepository.findById(setId);
		if (!os.isPresent()) {
			logger.info("[{}] Validating new setId FAIL - Reason: {} not existed --> END.", logId, setId);
			throw new ESafeboxException(EsafeboxErrorCode.SET_ATTRIBUTE_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.SET_ATTRIBUTE_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(setId))));
		}
		// boxTypePrice must > boxTypeSale
		if (!Objects.isNull(boxTypeSale) && boxTypeSale.compareTo(boxTypePrice) > 0) {
			logger.info(
					"[{}] Validating new boxTypePrice vs boxTypeSale FAIL - Reason: boxTypeSale {} > boxTypePrice{} existed --> END.",
					logId, boxTypeSale, boxTypePrice);
			throw new ESafeboxException(EsafeboxErrorCode.BOX_PRICE_SALE_IS_INVALID.getErrorCode(),
					EsafeboxErrorCode.BOX_PRICE_SALE_IS_INVALID
							.getDescription(Arrays.asList(String.valueOf(boxTypeSale), String.valueOf(boxTypePrice))));
		}
		Boxtype createdBoxType = Boxtype.builder().code(boxTypeCode).name(boxTypeName).setId(setId).set(os.get())
				.price(boxTypePrice).sale(boxTypeSale).icon(Objects.isNull(boxTypeIcon) ? null : boxTypeIcon.getBytes())
				.build();
		return boxTypeMapper.toBoxTypeDto(boxtypeDslRepository.save(createdBoxType));
	}

	public BoxTypeDto updateBoxType(Long logId, Map<String, Object> data) throws ESafeboxException {
//		logger.info("[{}] Updating boxtype {}", logId, data);
		Long boxTypeId = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
		String boxTypeName = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName().getFieldName(), data)
				.trim();
		Long boxTypePrice = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_PRICE.getEsafeboxFieldName().getFieldName(), data);
		Long boxTypeSale = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_SALE.getEsafeboxFieldName().getFieldName(), data);
		Long setId = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_SET.getEsafeboxFieldName().getFieldName(), data);
		String boxTypeIcon = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_ICON.getEsafeboxFieldName().getFieldName(), data);

		logger.info(
				"[{}] [Updating boxtype] boxTypeId:[{}], boxTypeName:[{}], boxTypePrice:[{}], boxTypeSale:[{}], setId:[{}]",
				logId, boxTypeId, boxTypeName, boxTypePrice, boxTypeSale, setId);

		// UC3: Boxtype exists
		Optional<Boxtype> obt = boxtypeDslRepository.findById(boxTypeId);
		if (!obt.isPresent()) {
			logger.info("[{}] Checking existing Boxtype FAIL - Reason: {} not exist --> END.", logId, boxTypeId);
			throw new ESafeboxException(EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(boxTypeId))));
		}
		Boxtype updatedBoxType = obt.get();
		// UC004: checking existing of setId
		if (!Objects.isNull(setId) && !updatedBoxType.getSetId().equals(setId)) {
			Optional<TbSet> os = setDslRepository.findById(setId);
			if (!os.isPresent()) {
				logger.info("[{}] Validating new setId FAIL - Reason: {} not existed --> END.", logId, setId);
				throw new ESafeboxException(EsafeboxErrorCode.SET_ATTRIBUTE_NOT_EXISTS.getErrorCode(),
						EsafeboxErrorCode.SET_ATTRIBUTE_NOT_EXISTS
								.getDescription(Arrays.asList(String.valueOf(setId))));
			}
			updatedBoxType.setSetId(setId);
			updatedBoxType.setSet(os.get());
		}
		if (!Objects.isNull(boxTypePrice)) {
			updatedBoxType.setPrice(boxTypePrice);
		}
		if (!Objects.isNull(boxTypeSale)) {
			// boxTypePrice must >= boxTypeSale
			if (boxTypeSale.compareTo(updatedBoxType.getPrice()) > 0) {
				logger.info(
						"[{}] Validating new boxTypePrice vs boxTypeSale FAIL - Reason: boxTypeSale {} > boxTypePrice{} existed --> END.",
						logId, boxTypeSale, boxTypePrice);
				throw new ESafeboxException(EsafeboxErrorCode.BOX_PRICE_SALE_IS_INVALID.getErrorCode(),
						EsafeboxErrorCode.BOX_PRICE_SALE_IS_INVALID.getDescription(
								Arrays.asList(String.valueOf(boxTypeSale), String.valueOf(boxTypePrice))));
			}
			updatedBoxType.setSale(boxTypeSale);
		}

		updatedBoxType.setName(boxTypeName);
		updatedBoxType.setIcon(Objects.isNull(boxTypeIcon) ? null : boxTypeIcon.getBytes());
		return boxTypeMapper.toBoxTypeDto(boxtypeDslRepository.save(updatedBoxType));
	}

	public PagedData<BoxTypeDto> getListBoxType(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list box type of {} with paging {}", logId, data, pageable);
		String boxTypeCode = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName().getFieldName(), data);
		String boxTypeName = EsafeboxUtils
				.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName().getFieldName(), data);
		Long boxTypePrice = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_PRICE.getEsafeboxFieldName().getFieldName(), data);
		Long boxTypeSale = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_SALE.getEsafeboxFieldName().getFieldName(), data);
		Long setId = EsafeboxUtils
				.getFieldValueAsLong(BoxTypeFieldName.BOX_TYPE_SET.getEsafeboxFieldName().getFieldName(), data);

		if (!Objects.isNull(boxTypeCode) && boxTypeCode.length() == 1) {
			logger.info(
					"[{}] Validating new setId FAIL - Reason: Data for field [{}] is not valid. The length must >= [{}] --> END.",
					logId, BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName().getFieldName(), "2");
			throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
					EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
							Arrays.asList(BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName().getFieldName(), "2")));
		}

		if (!Objects.isNull(boxTypeName) && boxTypeName.length() != 0 && boxTypeName.length() < 3) {
			logger.info(
					"[{}] Validating new setId FAIL - Reason: Data for field [{}] is not valid. The length must >= [{}] --> END.",
					logId, BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName().getFieldName(), "3");
			throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
					EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
							Arrays.asList(BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName().getFieldName(), "3")));
		}

		List<Boxtype> lstBoxType = boxtypeDslRepository.getListBoxType2(boxTypeCode, boxTypePrice, boxTypeSale, setId, pageable.getSort());
		String unsignedSearch = EsafeboxUtils.toUnsignedVietnamese(boxTypeName).toLowerCase();
		List<Boxtype> filteredBoxTypeLst = lstBoxType.stream().filter(t -> {
			return EsafeboxUtils.toUnsignedVietnamese(t.getName()).toLowerCase().contains(unsignedSearch);
		}).collect(Collectors.toList());
		
		int start = Long.valueOf(pageable.getOffset()).intValue();
		int end = (start + pageable.getPageSize()) > filteredBoxTypeLst .size() ? filteredBoxTypeLst .size() : (start + pageable.getPageSize());       

		Page<Boxtype> pageBoxType = new PageImpl<Boxtype>(filteredBoxTypeLst.subList(start, end), pageable, filteredBoxTypeLst.size());

		return PagedData.<BoxTypeDto>builder().totalElements(pageBoxType.getTotalElements())
				.totalPages(pageBoxType.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
				.pageSize(pageable.getPageSize()).data(boxTypeMapper.toBoxTypeDtos(pageBoxType.getContent())).build();

	}

	public BoxTypeDto getBoxTypeByBoxtypeCode(Long logId, String boxtypeCode) throws ESafeboxException {
		logger.info("[{}] Get box type by boxtypCode {}", logId, boxtypeCode);
		Optional<Boxtype> obt = boxtypeDslRepository.findBoxtypeByCode(boxtypeCode);
		if (!obt.isPresent()) {
			throw new ESafeboxException(EsafeboxErrorCode.BOXTYPE_CODE_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.BOXTYPE_CODE_NOT_EXISTS.getDescription(Arrays.asList(boxtypeCode)));
		}
		Boxtype boxType = obt.get();
		BoxTypeDto boxTypeDto = boxTypeMapper.toBoxTypeDto(boxType);
		if (!(Objects.isNull(boxType.getListBoxtypeValue()) || boxType.getListBoxtypeValue().isEmpty())) {
			boxTypeDto.setBoxTypeAttributes(
					boxTypeValueMapper.toBoxTypeValueDtos(new ArrayList<Value>(boxType.getListBoxtypeValue())));
		}
		return boxTypeDto;
	}
}
