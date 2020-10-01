package com.ecpay.esafebox.service;

import com.ecpay.entities.ecbox.*;
import com.ecpay.entities.ecbox.enumeration.AttributeDataType;
import com.ecpay.esafebox.dto.*;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.AttributeFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.AttributeMapper;
import com.ecpay.esafebox.mapper.AttributeSetMapper;
import com.ecpay.esafebox.repository.*;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttributeDslService {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
    public static final String DATE = "DATE";
    public static final String STRING = "STRING";
    public static final String NUMERIC = "NUMERIC";
    public static final Pattern DATE_TYPE_REGEX = Pattern.compile("^(YYYY:MM:DD)$|^(YYYY/MM/DD)$|^(YYYY-MM-DD)$|^(YYYYMMDD)$",Pattern.CASE_INSENSITIVE);
    public static final Pattern NUMERIC_TYPE_REGEX = Pattern.compile("^[0-9]{1,15}(\\.[0-9]{1,4})?$");

    @Autowired
    private AttributeDslRepository attributeDslRepository;
    @Autowired
    private AttributeSetDslRepository attributeSetDslRepository;
    @Autowired
    private SetDslRepository setDslRepository;
    @Autowired
    private UomDslRepository uomDslRepository;
    @Autowired
    private AttributeMapper attributeMapper;
    @Autowired
    private AttributeSetMapper attributeSetMapper;

    public AttributeDto createAttribute(Long logId, Map<String, Object> data) throws ESafeboxException {
        logger.info("[{}] Saving Attribute {}", logId, data);
        String attributeCode = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_CODE.getEsafeboxFieldName().getFieldName(), data);
        String attributeName = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_NAME.getEsafeboxFieldName().getFieldName(), data);
        String attributeFormat = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_FORMAT.getEsafeboxFieldName().getFieldName(), data);
        String attributeType = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_TYPE.getEsafeboxFieldName().getFieldName(), data);
        Long setId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_SET.getEsafeboxFieldName().getFieldName(), data);
        Long uomId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_UOM.getEsafeboxFieldName().getFieldName(), data);
        attributeCode = attributeCode.toUpperCase();

        //UC003: attribute_code is unique
        Optional<Attribute> obt = attributeDslRepository.findAttributeByCode(attributeCode.trim());
        if (obt.isPresent()) {
            logger.info("[{}] Validating new Attribute FAIL - Reason: {} existed --> END.", logId, attributeCode);
            throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_CODE_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.ATTRIBUTE_CODE_EXISTS.getDescription(Arrays.asList(attributeCode)));
        }
        //UC004: checking existing of setId
        Optional<TbSet> os = setDslRepository.findById(setId);
        if (!os.isPresent()) {
            logger.info("[{}] Validating new SetId FAIL - Reason: {} not existed --> END.", logId, setId);

            throw new ESafeboxException(EsafeboxErrorCode.SET_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.SET_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(setId))));
        }

        //UC005: checking existing of uomId
        Optional<Uom> uom = uomDslRepository.findById(uomId);

        if (!uom.isPresent()) {
            logger.info("[{}] Validating new UomId FAIL - Reason: {} not existed --> END.", logId, uomId);
            throw new ESafeboxException(EsafeboxErrorCode.UOM_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.UOM_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomId))));
        }

//        UC006: checking attributeType
        switch (attributeType) {
            case DATE: {
                if (!validateDate(attributeFormat)) {
                    logger.info("[{}] Validating AttributeType FAIL - Reason: {} wrong format --> END.", logId, attributeType);
                    throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_DATE_FORMAT_IS_INVALID.getErrorCode(),
                            EsafeboxErrorCode.ATTRIBUTE_DATE_FORMAT_IS_INVALID.getDescription(Arrays.asList(attributeType)));
                }
                break;
            }
            case STRING:
                break;
            case NUMERIC: {
                if (!validateNumeric(attributeFormat)) {
                    logger.info("[{}] Validating AttributeType FAIL - Reason: {} wrong format --> END.", logId, attributeType);
                    throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_NUMBERIC_FORMAT_IS_INVALID.getErrorCode(),
                            EsafeboxErrorCode.ATTRIBUTE_NUMBERIC_FORMAT_IS_INVALID.getDescription(Arrays.asList(attributeType)));
                }
                break;
            }
            default:
                logger.info("[{}] Validating AttributeType FAIL - Reason: {} wrong type --> END.", logId, attributeType);
                throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_WRONG_DATATYPE.getErrorCode(),
                        EsafeboxErrorCode.ATTRIBUTE_WRONG_DATATYPE.getDescription(Arrays.asList(attributeType)));
        }

        Attribute newAttribute = Attribute.builder()
                .code(attributeCode.trim())
                .name(attributeName.trim())
                .uomId(uomId)
                .uom(uom.get())
                .type(AttributeDataType.valueOf(attributeType))
                .format(attributeFormat)
                .build();
        attributeDslRepository.save(newAttribute);
        Long attributeId = newAttribute.getId();

        // UC005: checking existing AttributeSet by attributeId
        AttributeSet createAttributeSet;
        Optional<AttributeSet> as = attributeSetDslRepository.findAttributeSetByAttributeId(attributeId);
        if (as.isPresent()) {
            createAttributeSet = AttributeSet.builder()
                    .id(as.get().getId())
                    .attributeId(attributeId)
                    .setId(setId)
                    .build();
        } else {
            createAttributeSet = AttributeSet.builder()
                    .attributeId(attributeId)
                    .setId(setId)
                    .build();
        }
        attributeSetDslRepository.save(createAttributeSet);
        return attributeMapper.toAttributeDto(newAttribute);
    }

    public AttributeDto updateAttribute(long logId, Map<String, Object> data) throws Exception {
        logger.info("[{}] Saving Attribute {}", logId, data);
        Long attributeId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_ID.getEsafeboxFieldName().getFieldName(), data);
        String attributeName = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_NAME.getEsafeboxFieldName().getFieldName(), data);
        String attributeFormat = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_FORMAT.getEsafeboxFieldName().getFieldName(), data);
        String attributeType = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_TYPE.getEsafeboxFieldName().getFieldName(), data);
        Long setId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_SET.getEsafeboxFieldName().getFieldName(), data);
        Long uomId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_UOM.getEsafeboxFieldName().getFieldName(), data);

        //UC003: attribute is exist
        Optional<Attribute> att = attributeDslRepository.findById(attributeId);
        if (!att.isPresent()) {
            throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.ATTRIBUTE_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(attributeId))));
        }

        //UC004: checking existing of setId
        Optional<TbSet> os = setDslRepository.findById(setId);
        if (!os.isPresent()) {
            logger.info("[{}] Validating new SetId FAIL - Reason: {} not existed --> END.", logId, setId);

            throw new ESafeboxException(EsafeboxErrorCode.SET_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.SET_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(setId))));
        }

        // UC005: checking existing AttributeSet by attributeId
        AttributeSet updateAttributeSet;
        Optional<AttributeSet> as = attributeSetDslRepository.findAttributeSetByAttributeId(attributeId);
        if (as.isPresent()) {
            updateAttributeSet = AttributeSet.builder()
                    .id(as.get().getId())
                    .attributeId(attributeId)
                    .setId(setId)
                    .build();
        } else {
            updateAttributeSet = AttributeSet.builder()
                    .attributeId(attributeId)
                    .setId(setId)
                    .build();
        }
        attributeSetDslRepository.save(updateAttributeSet);

        //UC005: checking existing of uomId
        Optional<Uom> uom = uomDslRepository.findById(uomId);

        if (!uom.isPresent()) {
            logger.info("[{}] Validating new UomId FAIL - Reason: {} not existed --> END.", logId, uomId);
            throw new ESafeboxException(EsafeboxErrorCode.UOM_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.UOM_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(uomId))));
        }

//        UC006: checking attributeType
        switch (attributeType) {
            case DATE: {
                if (!validateDate(attributeFormat)) {
                    logger.info("[{}] Validating AttributeType FAIL - Reason: {} wrong format --> END.", logId, attributeType);
                    throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_DATE_FORMAT_IS_INVALID.getErrorCode(),
                            EsafeboxErrorCode.ATTRIBUTE_DATE_FORMAT_IS_INVALID.getDescription(Arrays.asList(attributeType)));
                }
                break;
            }
            case STRING:
                break;
            case NUMERIC: {
                if (!validateNumeric(attributeFormat)) {
                    System.out.println("ok");
                    logger.info("[{}] Validating AttributeType FAIL - Reason: {} wrong format --> END.", logId, attributeType);
                    throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_NUMBERIC_FORMAT_IS_INVALID.getErrorCode(),
                            EsafeboxErrorCode.ATTRIBUTE_NUMBERIC_FORMAT_IS_INVALID.getDescription(Arrays.asList(attributeType)));
                }
                break;
            }
            default:
                logger.info("[{}] Validating AttributeType FAIL - Reason: {} wrong type --> END.", logId, attributeType);
                throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_WRONG_DATATYPE.getErrorCode(),
                        EsafeboxErrorCode.ATTRIBUTE_WRONG_DATATYPE.getDescription(Arrays.asList(attributeType)));
        }

        Attribute updatedAttribute = att.get();
        if (!Objects.isNull(attributeFormat))
            updatedAttribute.setFormat(attributeFormat);
        if (!Objects.isNull(attributeName))
            updatedAttribute.setName(attributeName.trim());
        updatedAttribute.setType(AttributeDataType.valueOf(attributeType));
        updatedAttribute.setUomId(uomId);
        attributeDslRepository.save(updatedAttribute);
        return attributeMapper.toAttributeDto(updatedAttribute);
    }

    public PagedData<AttributeDto> getListAttribute(long logId, Map<String, Object> data, Pageable pageable) throws Exception {
        logger.info("[{}] Get ListAttribute of {} with paging {}", logId, data, pageable);
        Long attributeId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_ID.getEsafeboxFieldName().getFieldName(), data);
        String attributeCode = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_CODE.getEsafeboxFieldName().getFieldName(), data);
        String attributeName = EsafeboxUtils.getFieldValueAsString(AttributeFieldName.ATTRIBUTE_NAME.getEsafeboxFieldName().getFieldName(), data);
        Long setId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_SET.getEsafeboxFieldName().getFieldName(), data);
        Long uomId = EsafeboxUtils.getFieldValueAsLong(AttributeFieldName.ATTRIBUTE_UOM.getEsafeboxFieldName().getFieldName(), data);

//      UC0004 find by attributeId
        if (attributeId != null) {
            Optional<Attribute> attr = attributeDslRepository.findById(attributeId);
            if (!attr.isPresent()) {
                logger.info("[{}] check AttributeId", logId);
                throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_NOT_EXIST.getErrorCode(),
                        EsafeboxErrorCode.ATTRIBUTE_NOT_EXIST.getDescription(Arrays.asList(attributeCode)));
            }
        }

//        UC0005 find by attributeCode
        if ((attributeCode != null && !attributeCode.trim().isEmpty()) && attributeCode.length() < 2) {
            logger.info("[{}] check length of AttributeCode, must > 02 chars", logId);
            throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_CODE_IS_INVALID.getErrorCode(),
                    EsafeboxErrorCode.ATTRIBUTE_CODE_IS_INVALID.getDescription(Arrays.asList(attributeCode)));
        }

//        UC0006 find by attributeName
        if ((attributeName != null && !attributeName.trim().isEmpty()) && attributeName.length() < 3) {
            logger.info("[{}] check length of attributeName, must > 03 chars", logId);
            throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_NAME_IS_INVALID.getErrorCode(),
                    EsafeboxErrorCode.ATTRIBUTE_NAME_IS_INVALID.getDescription(Arrays.asList(attributeName)));
        }

        Page<Attribute> pageAttribute = attributeDslRepository.getListAttribute(attributeId, attributeCode, attributeName, uomId, pageable);

        List<AttributeDto> result = Collections.emptyList();
        if (pageAttribute.hasContent()) {
            result = pageAttribute.getContent().stream().map(t -> {
                AttributeDto aDto = attributeMapper.toAttributeDto(t);
                List<AttributeSetDto> attributeSetList = attributeSetMapper.toAttributeSetDtos(new ArrayList<>(t.getListAttrSet()));
                aDto.setAttributeSetList(attributeSetList);
                return aDto;
            }).collect(Collectors.toList());
        }

        return PagedData.<AttributeDto>builder()
                .totalElements(pageAttribute.getTotalElements())
                .totalPages(pageAttribute.getTotalPages())
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .data(result)
                .build();
    }

    public static boolean validateDate(String date) {
        Matcher matcher = DATE_TYPE_REGEX.matcher(date);
        return matcher.find();
    }

    public static boolean validateNumeric(String numeric) {
        Matcher matcher = NUMERIC_TYPE_REGEX.matcher(numeric);
        return matcher.find();
    }
}
