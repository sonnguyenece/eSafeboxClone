package com.ecpay.esafebox.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.uomtype.ListUomTypeRequest;
import com.ecpay.esafebox.controller.model.uomtype.ListUomTypeResponse;
import com.ecpay.esafebox.controller.model.uomtype.UomTypeCreateRequest;
import com.ecpay.esafebox.controller.model.uomtype.UomTypeCrudResponse;
import com.ecpay.esafebox.controller.model.uomtype.UomTypeUpdateRequest;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UomTypeDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UomTypeFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.UomtypeDslService;
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
public class UomTypeController {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
    @Autowired
    UomtypeDslService uomTypeDslService;

    @ApiOperation(value = "Return List Uom Type By Paging")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = ListUomTypeResponse.class)})
    @GetMapping(value = "/listuomtype", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listUomType(@RequestBody ListUomTypeRequest request) throws Exception {
        Gson gson = new Gson();
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][listUomType] Received request: {}", logId, gson.toJson(requestObj));
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validateForSearch(UomTypeFieldName.getSearchFieldNames(), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }

            Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);

            @SuppressWarnings("unchecked")
            List<String> sortBy = (sortsObject != null && sortsObject instanceof List && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
                    : Arrays.asList(UomTypeFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName());

            List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy, UomTypeFieldName.getListUomSortFieldNames());
            if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
                responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
                        Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
                                String.join(", ", Arrays.asList(UomTypeFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(),
                                        UomTypeFieldName.UOM_TYPE_CODE.getEsafeboxFieldName().getFieldName(),
                                        UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName().getFieldName()))));
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
            PagedData<UomTypeDto> pagedData = uomTypeDslService.getListUomType(logId, requestObj, pageable);
            pagedData.setSorts(sortBy);
            pagedData.setOrder(directionValue);
            if (CollectionUtils.isEmpty(pagedData.getData())) {
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(ResponseMessage.builder()
                        .responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
                        .responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
                        .responseTime(System.currentTimeMillis() - logId)
                        .build()));
                return response;
            }
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(pagedData)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][listUomType] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][listUomType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][listUomType][Duration: {}] Return response: {}", logId, endTime - logId, gson.toJson(response));
        }
        return response;
    }

    @ApiOperation(value = "Create Uomtype includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = UomTypeCrudResponse.class)})
    @PostMapping(value = "/createuomtype", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUomType(@RequestBody UomTypeCreateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][createUomType] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(UomTypeFieldName.UOM_TYPE_CODE.getEsafeboxFieldName(),
                            UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            UomTypeDto createdUomType = uomTypeDslService.createUomType(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(createdUomType)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createUomType] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createUomType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][createUomType][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Update Uomtype includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = UomTypeCrudResponse.class)})
    @PostMapping(value = "/updateuomtype", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUomType(@RequestBody UomTypeUpdateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][updateUomType] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(UomTypeFieldName.UOM_TYPE_ID.getEsafeboxFieldName(),
                            UomTypeFieldName.UOM_TYPE_NAME.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            UomTypeDto updatedUomType = uomTypeDslService.updateUomType(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(updatedUomType)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][updateUomType] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][updateUomType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][updateUomType][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }
}
