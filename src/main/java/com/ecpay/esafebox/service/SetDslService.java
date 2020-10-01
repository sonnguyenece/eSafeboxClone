package com.ecpay.esafebox.service;

import com.ecpay.entities.ecbox.TbSet;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.SetDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.SetFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.SetMapper;
import com.ecpay.esafebox.repository.SetDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SetDslService {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
    @Autowired
    private SetDslRepository setDslRepository;

    @Autowired
    private SetMapper setMapper;

    public SetDto createSet(Long logId, Map<String, Object> data) throws ESafeboxException {

        logger.info("[{}] Saving set {}", logId, data);
        String setCode = EsafeboxUtils.getFieldValueAsString(SetFieldName.SET_CODE.getEsafeboxFieldName().getFieldName(), data);
        String setName = EsafeboxUtils.getFieldValueAsString(SetFieldName.SET_NAME.getEsafeboxFieldName().getFieldName(), data);
        setCode = setCode.toUpperCase();

        //UC003: TbSetCode is unique
        Optional<TbSet> obt = setDslRepository.findSetByCode(setCode.trim());
        if (obt.isPresent()) {
            logger.info("[{}] Validating new TbSet FAIL - Reason: {} existed --> END.", logId, setCode);
            throw new ESafeboxException(EsafeboxErrorCode.SET_ATTRIBUTE_CODE_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.SET_ATTRIBUTE_CODE_EXISTS.getDescription(Arrays.asList(setCode)));
        }

        TbSet createdSet = TbSet.builder()
                .code(setCode.trim())
                .name(setName.trim())
                .build();
        return setMapper.toSetDto(setDslRepository.save(createdSet));
    }

    public SetDto updateSet(Long logId, Map<String, Object> data) throws ESafeboxException {
        logger.info("[{}] Updating TbSet {}", logId, data);
        Long setId = EsafeboxUtils.getFieldValueAsLong(SetFieldName.SET_ID.getEsafeboxFieldName().getFieldName(), data);
        String setName = EsafeboxUtils.getFieldValueAsString(SetFieldName.SET_NAME.getEsafeboxFieldName().getFieldName(), data);
        Long setCode = EsafeboxUtils.getFieldValueAsLong(SetFieldName.SET_CODE.getEsafeboxFieldName().getFieldName(), data);

        //UC003: TbSet exists
        Optional<TbSet> obt = setDslRepository.findById(setId);
        if (!obt.isPresent()) {
            logger.info("[{}] Checking existing TbSet FAIL - Reason: {} not exist --> END.", logId, setId);
            throw new ESafeboxException(EsafeboxErrorCode.SET_NOT_EXISTS.getErrorCode(),
                    EsafeboxErrorCode.SET_NOT_EXISTS.getDescription(Arrays.asList(String.valueOf(setId))));
        }
        TbSet updatedTbSet = obt.get();

        updatedTbSet.setName(setName.trim());

        return setMapper.toSetDto(setDslRepository.save(updatedTbSet));
    }

    public PagedData<SetDto> getListSet(Long logId, Map<String, Object> data, Pageable pageable) throws Exception {
        logger.info("[{}] Get list set of {} with paging {}", logId, data, pageable);
        String setCode = EsafeboxUtils.getFieldValueAsString(SetFieldName.SET_CODE.getEsafeboxFieldName().getFieldName(), data);
        String setName = EsafeboxUtils.getFieldValueAsString(SetFieldName.SET_NAME.getEsafeboxFieldName().getFieldName(), data);
        Long setId = EsafeboxUtils.getFieldValueAsLong(SetFieldName.SET_ID.getEsafeboxFieldName().getFieldName(), data);
//        UC0005 find by setCode
        if ((setCode != null && !setCode.trim().isEmpty()) && setCode.length() < 2) {
            logger.info("[{}] check length of setCode, must > 02 chars", logId);
            throw new ESafeboxException(EsafeboxErrorCode.SET_CODE_IS_INVALID.getErrorCode(),
                    EsafeboxErrorCode.SET_CODE_IS_INVALID.getDescription(Arrays.asList(setCode)));
        }
//        UC0006 find by setName
        if ((setName != null && !setName.trim().isEmpty()) && setName.length() < 3) {
            logger.info("[{}] check length of setName, must > 03 chars", logId);
            throw new ESafeboxException(EsafeboxErrorCode.SET_NAME_IS_INVALID.getErrorCode(),
                    EsafeboxErrorCode.SET_NAME_IS_INVALID.getDescription(Arrays.asList(setName)));
        }

        Page<TbSet> pageSet = setDslRepository.getListSet(setId, setCode, setName, pageable);
        return PagedData.<SetDto>builder()
                .totalElements(pageSet.getTotalElements())
                .totalPages(pageSet.getTotalPages())
                .pageNumber(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .data(setMapper.toSetDtos(pageSet.getContent()))
                .build();

    }
}
