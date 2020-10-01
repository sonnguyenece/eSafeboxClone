package com.ecpay.esafebox.service;

import java.util.*;
import java.util.stream.Collectors;

import com.ecpay.esafebox.dto.*;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxTypeFieldName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecpay.entities.ecbox.Attribute;
import com.ecpay.entities.ecbox.Boxtype;
import com.ecpay.entities.ecbox.Limit;
import com.ecpay.entities.ecbox.LimitValue;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.LimitFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.LimitValueFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.LimitMapper;
import com.ecpay.esafebox.mapper.LimitValueMapper;
import com.ecpay.esafebox.repository.AttributeDslRepository;
import com.ecpay.esafebox.repository.BoxtypeDslRepository;
import com.ecpay.esafebox.repository.LimitDslRepository;
import com.ecpay.esafebox.repository.LimitValueDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class LimitDslService {

    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
    @Autowired
    private LimitDslRepository limitDslRepository;
    @Autowired
    private LimitValueDslRepository limitValueDslRepository;
    @Autowired
    private BoxtypeDslRepository boxtypeDslRepository;
    @Autowired
    private AttributeDslRepository attributeDslRepository;
    @Autowired
    private LimitMapper limitMapper;
    @Autowired
    private LimitValueMapper limitValueMapper;

    public LimitCreateResponseDto createBoxTypeLimit(Long logId, Map<String, Object> data) throws ESafeboxException {
        logger.info("[{}] createBoxTypeLimit {}", logId, data);
        String limitCode = EsafeboxUtils
                .getFieldValueAsString(LimitFieldName.LIMIT_CODE.getEsafeboxFieldName().getFieldName(), data).trim().toUpperCase();
        String limitName = EsafeboxUtils
                .getFieldValueAsString(LimitFieldName.LIMIT_NAME.getEsafeboxFieldName().getFieldName(), data).trim();
        Long limitType = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_TYPE.getEsafeboxFieldName().getFieldName(), data);
        Long boxTypeId = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_BOX_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
        Long limitValue = EsafeboxUtils
                .getFieldValueAsLong(LimitValueFieldName.LIMIT_VALUE.getEsafeboxFieldName().getFieldName(), data);
        Long attributeId = EsafeboxUtils
                .getFieldValueAsLong(LimitValueFieldName.ATTRIBUTE_ID.getEsafeboxFieldName().getFieldName(), data);

        // check UC003 throw S028
        Optional<Limit> ol = limitDslRepository.findLimitByCode(limitCode);
        if (ol.isPresent()) {
            logger.info("[{}] Validating new box type limit FAIL - Reason: {} existed --> END.", logId, limitCode);
            throw new ESafeboxException(EsafeboxErrorCode.LIMIT_CODE_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.LIMIT_CODE_EXISTS.getDescription(Arrays.asList(limitCode)));
        }

        // check UC005 throw S018
        Optional<Boxtype> obt = boxtypeDslRepository.findById(boxTypeId);
        if (!obt.isPresent()) {
            logger.info("[{}] Validating new box type limit FAIL - Reason: {} does not exist --> END.", logId,
                    boxTypeId);
            throw new ESafeboxException(EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(boxTypeId))));
        }

        // check UC006 throw S015
        Optional<Attribute> oa = attributeDslRepository.findAttributeById(attributeId);
        if (!oa.isPresent()) {
            logger.info("[{}] Validating new box type limit FAIL - Reason: {} does not exist --> END.", logId,
                    attributeId);
            throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_ID_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.ATTRIBUTE_ID_NOT_EXISTS
                            .getDescription(Arrays.asList(String.valueOf(attributeId))));
        }

        Limit createdLimit = Limit.builder().code(limitCode).name(limitName).type(limitType).boxTypeId(boxTypeId)
                .build();
        limitDslRepository.save(createdLimit);

        LimitValue createdLimitValue = LimitValue.builder().limitId(createdLimit.getId()).value(limitValue)
                .attributeId(attributeId).build();
        limitValueDslRepository.save(createdLimitValue);
        List<LimitValue> l = new ArrayList<LimitValue>();
        l.add(createdLimitValue);
        createdLimit.setListLimitValue(l);

        return limitMapper.toLimitCreateResponseDto(createdLimit, createdLimitValue);
    }

    public LimitCreateResponseDto updateLimit(Long logId, Map<String, Object> data) throws ESafeboxException {
        logger.info("[{}] Updating boxtype limit {}", logId, data);
        Long limitId = EsafeboxUtils.getFieldValueAsLong(LimitFieldName.LIMIT_ID.getEsafeboxFieldName().getFieldName(),
                data);
        String limitName = EsafeboxUtils
                .getFieldValueAsString(LimitFieldName.LIMIT_NAME.getEsafeboxFieldName().getFieldName(), data);
        Long limitType = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_TYPE.getEsafeboxFieldName().getFieldName(), data);
        Long boxTypeId = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_BOX_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);
        Long limitValueId = EsafeboxUtils
                .getFieldValueAsLong(LimitValueFieldName.LIMIT_VALUE_ID.getEsafeboxFieldName().getFieldName(), data);
        Long limitValue = EsafeboxUtils
                .getFieldValueAsLong(LimitValueFieldName.LIMIT_VALUE.getEsafeboxFieldName().getFieldName(), data);
        Long attributeId = EsafeboxUtils
                .getFieldValueAsLong(LimitValueFieldName.ATTRIBUTE_ID.getEsafeboxFieldName().getFieldName(), data);

        // UC003: check limit, throw S029
        Optional<Limit> ol = limitDslRepository.findById(limitId);
        if (!ol.isPresent()) {
            logger.info("[{}] Checking existing Boxtype FAIL - Reason: {} not exist --> END.", logId, limitId);
            throw new ESafeboxException(EsafeboxErrorCode.LIMIT_ID_DOES_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.LIMIT_ID_DOES_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(limitId))));
        }

        // check UC005 throw S018
        Optional<Boxtype> obt = boxtypeDslRepository.findById(boxTypeId);
        if (!obt.isPresent()) {
            logger.info("[{}] Validating new box type limit FAIL - Reason: {} does not exist --> END.", logId,
                    boxTypeId);
            throw new ESafeboxException(EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(boxTypeId))));
        }

        // UC006: check limit, throw S029
        Optional<LimitValue> olv = limitValueDslRepository.findLimitValueById(limitValueId);
        if (!olv.isPresent()) {
            logger.info("[{}] Checking existing Boxtype FAIL - Reason: {} not exist --> END.", logId, limitValueId);
            throw new ESafeboxException(EsafeboxErrorCode.LIMIT_VALUE_ID_DOES_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.LIMIT_VALUE_ID_DOES_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(limitValueId))));
        }

        // check UC007 throw S015
        Optional<Attribute> oa = attributeDslRepository.findAttributeById(attributeId);
        if (!oa.isPresent()) {
            logger.info("[{}] Validating new box type limit FAIL - Reason: {} does not exist --> END.", logId,
                    attributeId);
            throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_ID_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.ATTRIBUTE_ID_NOT_EXISTS
                            .getDescription(Arrays.asList(String.valueOf(attributeId))));
        }

        LimitValue updatedLimitValue = olv.get();
        updatedLimitValue.setAttributeId(attributeId);
        updatedLimitValue.setValue(limitValue);
        limitValueDslRepository.save(updatedLimitValue);

        Limit updatedLimit = ol.get();
        updatedLimit.setName(limitName);
        updatedLimit.setType(limitType);

        return limitMapper.toLimitCreateResponseDto(updatedLimit, updatedLimitValue);
    }

    public PagedData<LimitDto> getListBoxTypeLimit(Long logId, Map<String, Object> data, Pageable pageable)
            throws ESafeboxException {
        logger.info("[{}] Get list box type limit of {} with paging {}", logId, data, pageable);

        Long limitId = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_ID.getEsafeboxFieldName().getFieldName(), data);
        String limitCode = EsafeboxUtils
                .getFieldValueAsString(LimitFieldName.LIMIT_CODE.getEsafeboxFieldName().getFieldName(), data);
        String limitName = EsafeboxUtils
                .getFieldValueAsString(LimitFieldName.LIMIT_NAME.getEsafeboxFieldName().getFieldName(), data);
        Long limitType = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_TYPE.getEsafeboxFieldName().getFieldName(), data);
        Long boxTypeId = EsafeboxUtils
                .getFieldValueAsLong(LimitFieldName.LIMIT_BOX_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);

        // UC0004: limitId does not exist
        if (!Objects.isNull(limitId) && !StringUtils.isEmpty(limitId)) {
            Optional<Limit> obt = limitDslRepository.findById(limitId);
            if (!obt.isPresent()) {
                logger.info("[{}] Checking existing limitId FAIL - Reason: {} does not exist --> END.", logId, limitId);
                throw new ESafeboxException(EsafeboxErrorCode.LIMIT_ID_DOES_NOT_EXISTS.getErrorCode(),
                        EsafeboxErrorCode.LIMIT_ID_DOES_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(limitId))));
            }
        }

        //UC0005: boxTypeId does not exist
        if (!Objects.isNull(boxTypeId) && !StringUtils.isEmpty(boxTypeId)) {
            Optional<Boxtype> obt = boxtypeDslRepository.findById(boxTypeId);
            if (!obt.isPresent()) {
                logger.info("[{}] Checking existing boxTypeId FAIL - Reason: {} does not exist --> END.", logId, boxTypeId);
                throw new ESafeboxException(EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getErrorCode(),
                        EsafeboxErrorCode.BOXTYPE_ID_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(boxTypeId))));
            }
        }

        //UC0007: limitName
        if (!Objects.isNull(limitName) && !StringUtils.isEmpty(limitName) && limitName.length() < 3) {
            logger.info("[{}] Checking existing limitName FAIL - Reason: {} limitName length must >= 3 --> END.", logId, limitName);
            throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getErrorCode(),
                    EsafeboxErrorCode.DATA_IS_INVALID_LENGTH_2.getDescription(
                            Arrays.asList(LimitFieldName.LIMIT_NAME.getEsafeboxFieldName().getFieldName(), "3")));
        }

        Page<Limit> pageLimit = limitDslRepository.getListBoxTypeLimit(limitId, limitCode, limitName, limitType,
                boxTypeId, pageable);
        List<LimitDto> result = Collections.emptyList();
        if (pageLimit.hasContent()) {
            result = pageLimit.getContent().stream().map(t -> {
                LimitDto lmDto = limitMapper.toLimitDto(t);
                //LimitRemoveReponseDto lmDto = limitMapper.toLimitRemoveReponseDto(t);
                List<LimitValueDto> limitValueList = limitValueMapper
                        .toLimitValueDtos(t.getListLimitValue().stream().collect(Collectors.toList()));
                lmDto.setListLimitValue(limitValueList);
                return lmDto;
            }).collect(Collectors.toList());
        }
        return PagedData.<LimitDto>builder().totalElements(pageLimit.getTotalElements())
                .totalPages(pageLimit.getTotalPages()).pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize()).data(result).build();

    }

    public LimitRemoveReponseDto removeLimit(long logId, Map<String, Object> data) throws ESafeboxException {
        logger.info("[{}] Removing boxtype limit {}", logId, data);
        Long limitId = EsafeboxUtils.getFieldValueAsLong(LimitFieldName.LIMIT_ID.getEsafeboxFieldName().getFieldName(),
                data);

        // UC003: check limit, throw S029
        Optional<Limit> ol = limitDslRepository.findById(limitId);
        if (!ol.isPresent()) {
            logger.info("[{}] Checking existing Boxtype FAIL - Reason: {} not exist --> END.", logId, limitId);
            throw new ESafeboxException(EsafeboxErrorCode.LIMIT_ID_DOES_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.LIMIT_ID_DOES_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(limitId))));
        }

        // reponse after delete
        LimitRemoveReponseDto lmDto = limitMapper.toLimitRemoveReponseDto(ol.get());
        List<LimitValueDto> limitValueList = limitValueMapper
                .toLimitValueDtos(ol.get().getListLimitValue().stream().collect(Collectors.toList()));
        lmDto.setListLimitValue(limitValueList);

        //delete
        limitDslRepository.deleteById(limitId);

        return lmDto;
    }
}
