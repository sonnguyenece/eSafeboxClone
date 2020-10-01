package com.ecpay.esafebox.controller;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.attribute.*;
import com.ecpay.esafebox.dto.AttributeDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.AttributeFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.AttributeDslService;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@Api(value = "API(s) for Attribute", description = "Operations maintaining the Attribute")
public class AttributeController {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);

    @Autowired
    private AttributeDslService attributeDslService;

    @ApiOperation(value = "Create Attribute")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = AttributeCrudResponse.class)})
    @PostMapping(value = "/createattribute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAttribute(@RequestBody AttributeCreateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][createAttribute] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(
                            AttributeFieldName.ATTRIBUTE_CODE.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_NAME.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_FORMAT.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_TYPE.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_SET.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_UOM.getEsafeboxFieldName()),
                            requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            AttributeDto createAttribute = attributeDslService.createAttribute(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(createAttribute)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createAttribute] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createAttribute] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][createAttribute][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Update Attribute")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = AttributeCrudResponse.class)})
    @PostMapping(value = "/updateattribute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAttribute(@RequestBody AttributeUpdateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][updateAttribute] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(
                            AttributeFieldName.ATTRIBUTE_ID.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_NAME.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_FORMAT.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_TYPE.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_SET.getEsafeboxFieldName(),
                            AttributeFieldName.ATTRIBUTE_UOM.getEsafeboxFieldName()),
                            requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            AttributeDto createAttribute = attributeDslService.updateAttribute(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(createAttribute)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][updateAttribute] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][updateAttribute] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][updateAttribute][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "List Attribute")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = AttributeListResponse.class)})
    @GetMapping(value = "/listattribute", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listAttribute(@RequestBody AttributeListRequest request) throws Exception {
        Gson gson = new Gson();
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][listAttribute] Received request: {}", logId, gson.toJson(requestObj));
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validateForSearch(AttributeFieldName.getSearchFieldNames(), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }

            Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);

            @SuppressWarnings("unchecked")
            List<String> sortBy = (sortsObject != null && sortsObject instanceof List && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
                    : Arrays.asList(AttributeFieldName.ATTRIBUTE_ID.getEsafeboxFieldName().getFieldName());

            List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy, AttributeFieldName.getListSetSortFieldNames());
            if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
                responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
                        Arrays.asList(
                                PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
                                String.join(", ",
                                        Arrays.asList(
                                                AttributeFieldName.ATTRIBUTE_ID.getEsafeboxFieldName().getFieldName(),
                                                AttributeFieldName.ATTRIBUTE_CODE.getEsafeboxFieldName().getFieldName(),
                                                AttributeFieldName.ATTRIBUTE_NAME.getEsafeboxFieldName().getFieldName(),
                                                AttributeFieldName.ATTRIBUTE_UOM.getEsafeboxFieldName().getFieldName()
                                        ))));
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
            PagedData<AttributeDto> attributePagedData = attributeDslService.getListAttribute(logId, requestObj, pageable);
            attributePagedData.setSorts(sortBy);
            attributePagedData.setOrder(directionValue);
            if (CollectionUtils.isEmpty(attributePagedData.getData())) {
                response = ResponseEntity.ok(EsafeboxUtils
                        .buildStandardResponse(ResponseMessage
                                .builder()
                                .responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
                                .responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
                                .responseTime(System.currentTimeMillis() - logId)
                                .build()));
                return response;
            }
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(attributePagedData)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build()));
        } catch (ESafeboxException e) {
            logger.error("[{}][listAttribute] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][listAttribute] Exception: {}", logId, e.getMessage(), e);
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(ResponseMessage.builder()
                            .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                            .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                            .responseTime(System.currentTimeMillis() - logId).build()));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][listAttribute][Duration: {}] Return response: {}", logId, endTime - logId, gson.toJson(response));
        }
        return response;
    }
}
