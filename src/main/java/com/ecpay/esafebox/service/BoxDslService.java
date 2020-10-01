package com.ecpay.esafebox.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ecpay.entities.ecbox.Box;
import com.ecpay.esafebox.dto.BoxDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.BoxMapper;
import com.ecpay.esafebox.repository.BoxDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.TimeUtils;

@Service
@Transactional
public class BoxDslService {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	@Autowired
	private BoxDslRepository boxDslRepository;

	@Autowired
	private BoxMapper boxMapper;

	public Box createOrUpdateManufacture(Box entity) {
		return boxDslRepository.save(entity);
	}
//
//	public String createBoxes(Long logId, Long manufactureId, Long quantityBox) throws ESafeboxException {
//		logger.info("[{}] createBoxes: manufactureId {}, quantityBox {}", logId, manufactureId, quantityBox);
//		String err_code = boxDslRepository.createBoxes(manufactureId, quantityBox);
//		if (!err_code.equals("0000")) {
//			logger.error("[{}] Exception: {}", logId, err_code);
//			throw new ESafeboxException("0001", err_code);
//		}
//		return err_code;
//	}

	public PagedData<BoxDto> getListBox(Long logId, Map<String, Object> data, Pageable pageable)
			throws ESafeboxException {
		logger.info("[{}] Get list box of {} with paging {}", logId, data, pageable);
		try {
			String manufactureCode = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName(), data);
			Long boxTypeId = EsafeboxUtils
					.getFieldValueAsLong(BoxFieldName.BOX_TYPE.getEsafeboxFieldName().getFieldName(), data);
			if (Objects.isNull(boxTypeId) || StringUtils.isEmpty(boxTypeId)) {
				boxTypeId = Long.valueOf(0l);
			}
			Long boxSerial = EsafeboxUtils
					.getFieldValueAsLong(BoxFieldName.BOX_SERIAL.getEsafeboxFieldName().getFieldName(), data);
			String boxStatus = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.BOX_STATUS.getEsafeboxFieldName().getFieldName(), data);
			if (Objects.isNull(boxStatus) || StringUtils.isEmpty(boxStatus)) {
				boxStatus = "0";
			}
			String fDate = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName(), data);
			String tDate = EsafeboxUtils
					.getFieldValueAsString(BoxFieldName.TO_DATE.getEsafeboxFieldName().getFieldName(), data);
			LocalDateTime fromDate = null;
			LocalDateTime toDate = null;
			fromDate = Objects.isNull(fDate) ? null : TimeUtils.convertString2LocalDate(fDate).atStartOfDay();
			toDate = Objects.isNull(toDate) ? null : TimeUtils.convertString2LocalDate(tDate).atTime(LocalTime.MAX);
			Page<Box> pageBox = boxDslRepository.getListBox(manufactureCode, boxTypeId, boxSerial, boxStatus, fromDate,
					toDate, pageable);
			return PagedData.<BoxDto>builder().totalElements(pageBox.getTotalElements())
					.totalPages(pageBox.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
					.pageSize(pageable.getPageSize()).data(boxMapper.toBoxDtos(pageBox.getContent())).build();
		} catch (Exception e) {
			logger.error("[{}] Exception: {}", logId, e.getMessage(), e);
			throw new ESafeboxException("0001", e.getMessage());
		}

	}
}
