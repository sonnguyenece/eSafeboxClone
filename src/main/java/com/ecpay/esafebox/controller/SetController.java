package com.ecpay.esafebox.controller;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.set.*;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.SetDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.SetFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.SetDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PaginationUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequiredArgsConstructor
@RestController
@Api(value = "API(s) for Set", description = "Operations maintaining the Set")
public class SetController {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);

    @Autowired
    private SetDslService setDslService;

    @ApiOperation(value = "Return List Set By Paging")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = ListSetResponse.class)})
    @GetMapping(value = "/listset", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listSet(@RequestBody ListSetRequest request) throws Exception {
        Gson gson = new Gson();
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][listSet] Received request: {}", logId, gson.toJson(requestObj));
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here

        try {
            responseMessage = ValidatorData.validateForSearch(SetFieldName.getSearchFieldNames(), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }

            Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);

            @SuppressWarnings("unchecked")
            List<String> sortBy = (sortsObject != null && sortsObject instanceof List && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
                    : Arrays.asList(SetFieldName.SET_ID.getEsafeboxFieldName().getFieldName());

            List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy, SetFieldName.getListSetSortFieldNames());
            if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
                responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
                        Arrays.asList(
                                PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
                                String.join(", ",
                                        Arrays.asList(
                                                SetFieldName.SET_ID.getEsafeboxFieldName().getFieldName(),
                                                SetFieldName.SET_CODE.getEsafeboxFieldName().getFieldName(),
                                                SetFieldName.SET_NAME.getEsafeboxFieldName().getFieldName()))));
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            //direction
            String directionValue = EsafeboxUtils.getFieldValueAsString(PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getFieldName(), requestObj);
            String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue().toString();
            directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
            Pageable pageable = PaginationUtils.buildPageable(requestObj, sortByEntityFieldName, directionValue);

            //do search
            PagedData<SetDto> setPagedData = setDslService.getListSet(logId, requestObj, pageable);
            setPagedData.setSorts(sortBy);
            setPagedData.setOrder(directionValue);
            if (CollectionUtils.isEmpty(setPagedData.getData())) {
                responseMessage = ResponseMessage
                        .builder()
                        .responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
                        .responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
                        .responseTime(System.currentTimeMillis() - logId).build();
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(setPagedData)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build()));
        } catch (ESafeboxException e) {
            logger.error("[{}][listSet] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][listSet] Exception: {}", logId, e.getMessage(), e);
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(ResponseMessage.builder()
                            .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                            .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                            .responseTime(System.currentTimeMillis() - logId).build()));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][listSet][Duration: {}] Return response: {}", logId, endTime - logId, gson.toJson(response));
        }
        return response;
    }

    @ApiOperation(value = "Create Set")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = SetCrudResponse.class)})
    @PostMapping(value = "/createset", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSet(@RequestBody SetCreateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][createSet] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(
                            SetFieldName.SET_CODE.getEsafeboxFieldName(),
                            SetFieldName.SET_NAME_CREATE_UPDATE.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            SetDto createSet = setDslService.createSet(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(createSet)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createSet] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createSet] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][createSet][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Update Set")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = SetCrudResponse.class)})
    @PostMapping(value = "/updateset", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateSet(@RequestBody SetUpdateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][updateSet] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(SetFieldName.SET_ID.getEsafeboxFieldName(),
                            SetFieldName.SET_NAME_CREATE_UPDATE.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            SetDto updateSetDto = setDslService.updateSet(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(updateSetDto)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][updateSet] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][updateSet] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][updateSet][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;

    }
}