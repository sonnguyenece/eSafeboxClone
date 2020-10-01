package com.ecpay.esafebox.controller;

import java.util.*;

import com.ecpay.esafebox.controller.model.limit.*;
import com.ecpay.esafebox.dto.LimitRemoveReponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.dto.LimitCreateResponseDto;
import com.ecpay.esafebox.dto.LimitDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.LimitFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.LimitValueFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.LimitDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PaginationUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class LimitController {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
    @Autowired
    LimitDslService limitDslService;

    @ApiOperation(value = "Return List Box Type Limit By Paging")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = ListBoxTypeLimitResponse.class)})
    @GetMapping(value = "/listboxtypelimit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getListBoxTypeLimit(@RequestBody ListBoxTypeLimitRequest request) throws Exception {
        Gson gson = new Gson();
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][listBoxTypeLimit] Received request: {}", logId, gson.toJson(requestObj));
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validateForSearch(LimitFieldName.getSearchFieldNames(), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }

            Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(
                    PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);

            @SuppressWarnings("unchecked")
            List<String> sortBy = (sortsObject != null && sortsObject instanceof List
                    && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
                    : Arrays.asList(LimitFieldName.LIMIT_ID.getEsafeboxFieldName().getFieldName());

            List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy,
                    LimitFieldName.getListBoxTypeLimitSortFieldNames());
            if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
                responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(
                        EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
                        Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
                                String.join(", ", Arrays.asList(
                                        LimitFieldName.LIMIT_ID.getEsafeboxFieldName().getFieldName(),
                                        LimitFieldName.LIMIT_CODE.getEsafeboxFieldName().getFieldName(),
                                        LimitFieldName.LIMIT_NAME.getEsafeboxFieldName().getFieldName(),
                                        LimitFieldName.LIMIT_TYPE.getEsafeboxFieldName().getFieldName(),
                                        LimitFieldName.LIMIT_BOX_TYPE_ID.getEsafeboxFieldName().getFieldName()))));
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            // direction
            String directionValue = EsafeboxUtils.getFieldValueAsString(
                    PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getFieldName(), requestObj);
            String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue()
                    .toString();
            directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
            Pageable pageable = PaginationUtils.buildPageable(requestObj, sortByEntityFieldName, directionValue);
            // do search
            PagedData<LimitDto> pagedData = limitDslService.getListBoxTypeLimit(logId, requestObj, pageable);
            pagedData.setSorts(sortBy);
            pagedData.setOrder(directionValue);
            if (CollectionUtils.isEmpty(pagedData.getData())) {
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(
                        ResponseMessage.builder().responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
                                .responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
                                .responseTime(System.currentTimeMillis() - logId).build()));
                return response;
            }
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(pagedData)
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][listBoxTypeLimit] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][listBoxTypeLimit] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][listBoxTypeLimit][Duration: {}] Return response: {}", logId, endTime - logId,
                    gson.toJson(response));
        }
        return response;
    }

    @ApiOperation(value = "Create Box Type Limit includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = BoxTypeLimitCrudResponse.class)})
    @PostMapping(value = "/createboxtypelimit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBoxTypeLimit(@RequestBody BoxTypeLimitCreateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][createBoxTypeLimit] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validate(Arrays.asList(LimitFieldName.LIMIT_CODE.getEsafeboxFieldName(),
                    LimitFieldName.LIMIT_NAME.getEsafeboxFieldName(), LimitFieldName.LIMIT_TYPE.getEsafeboxFieldName(),
                    LimitFieldName.LIMIT_BOX_TYPE_ID.getEsafeboxFieldName(),
                    LimitValueFieldName.LIMIT_VALUE.getEsafeboxFieldName(),
                    LimitValueFieldName.ATTRIBUTE_ID.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            LimitCreateResponseDto createdLimit = limitDslService.createBoxTypeLimit(logId, requestObj);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(createdLimit)
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createBoxTypeLimit] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createBoxTypeLimit] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][createBoxTypeLimit][Duration: {}] Return response: {}", logId, endTime - logId,
                    responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Update Box Type Limit includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = BoxTypeLimitCrudResponse.class)})
    @PostMapping(value = "/updateboxtypelimit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBoxTypeLimit(@RequestBody BoxTypeLimitUpdateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][updateBoxTypeLimit] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validate(Arrays.asList(LimitFieldName.LIMIT_ID.getEsafeboxFieldName(),
                    LimitFieldName.LIMIT_NAME.getEsafeboxFieldName(), LimitFieldName.LIMIT_TYPE.getEsafeboxFieldName(),
                    LimitFieldName.LIMIT_BOX_TYPE_ID.getEsafeboxFieldName(),
                    LimitValueFieldName.LIMIT_VALUE_ID.getEsafeboxFieldName(),
                    LimitValueFieldName.LIMIT_VALUE.getEsafeboxFieldName(),
                    LimitValueFieldName.ATTRIBUTE_ID.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            LimitCreateResponseDto updatedLimit = limitDslService.updateLimit(logId, requestObj);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(updatedLimit)
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][updateBoxTypeLimit] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][updateBoxTypeLimit] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][updateBoxTypeLimit][Duration: {}] Return response: {}", logId, endTime - logId,
                    responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Remove Box Type Limit includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = BoxTypeLimitRemoveReponse.class)})
    @PostMapping(value = "/removeboxtypelimit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeBoxTypeLimit(@RequestBody BoxTypeLimitRemoveRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][removeBoxTypeLimit] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;

        // additional request validation here
        try {
            responseMessage = ValidatorData.validate(Arrays.asList(LimitFieldName.LIMIT_ID.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            LimitRemoveReponseDto removeLimit = limitDslService.removeLimit(logId, requestObj);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(removeLimit)
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][removeBoxTypeLimit] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][removeBoxTypeLimit] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][removeBoxTypeLimit][Duration: {}] Return response: {}", logId, endTime - logId,
                    responseMessage);
        }

        return response;
    }
}
