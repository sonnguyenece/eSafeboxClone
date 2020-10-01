package com.ecpay.esafebox.controller;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
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
import com.ecpay.esafebox.controller.model.boxtype.BoxTypeCreateRequest;
import com.ecpay.esafebox.controller.model.boxtype.BoxTypeCrudResponse;
import com.ecpay.esafebox.controller.model.boxtype.BoxTypeInquiryRequest;
import com.ecpay.esafebox.controller.model.boxtype.BoxTypeInquiryResponse;
import com.ecpay.esafebox.controller.model.boxtype.BoxTypeUpdateRequest;
import com.ecpay.esafebox.controller.model.boxtype.ListBoxTypeRequest;
import com.ecpay.esafebox.controller.model.boxtype.ListBoxTypeResponse;
import com.ecpay.esafebox.dto.BoxTypeDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxTypeFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.BoxtypeDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PaginationUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RequiredArgsConstructor
@RestController
@Api(value = "API(s) for Box Type", description = "Operations maintaining the Box's Type")
public class BoxTypeController {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
    @Autowired
    BoxtypeDslService boxTypeDslService;

    @ApiOperation(value = "Return List Box Type By Paging")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = ListBoxTypeResponse.class)})
    @GetMapping(value = "/listboxtype", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listBoxType(@RequestBody ListBoxTypeRequest request) throws Exception {
        Gson gson = new Gson();
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][listBoxType] Received request: {}", logId, gson.toJson(requestObj));
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validateForSearch(BoxTypeFieldName.getSearchFieldNames(), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);

            @SuppressWarnings("unchecked")
            List<String> sortBy = (sortsObject != null && sortsObject instanceof List && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
                    : Arrays.asList(BoxTypeFieldName.BOX_TYPE_ID.getEsafeboxFieldName().getFieldName());

            List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy, BoxTypeFieldName.getListBoxSortFieldNames());
            if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty() || sortBy.size() != sortByEntityFieldName.size()) {
                responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
                        Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
                                String.join(", ", Arrays.asList(BoxTypeFieldName.BOX_TYPE_ID.getEsafeboxFieldName().getFieldName(),
                                        BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName().getFieldName(),
                                        BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName().getFieldName(),
                                        BoxTypeFieldName.BOX_TYPE_PRICE.getEsafeboxFieldName().getFieldName(),
                                        BoxTypeFieldName.BOX_TYPE_SALE.getEsafeboxFieldName().getFieldName(),
                                        BoxTypeFieldName.BOX_TYPE_SET.getEsafeboxFieldName().getFieldName()))));
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
            PagedData<BoxTypeDto> pagedData = boxTypeDslService.getListBoxType(logId, requestObj, pageable);
            pagedData.setSorts(sortBy);
            pagedData.setOrder(directionValue);
            if (CollectionUtils.isEmpty(pagedData.getData())) {
                responseMessage = ResponseMessage.builder()
                        .responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
                        .responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
                        .responseData(Collections.emptyMap())
                        .responseTime(System.currentTimeMillis() - logId)
                        .build();
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
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
            logger.error("[{}][listBoxType] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][listBoxType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][listBoxType][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Get Boxtype by Boxtype's Code")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = BoxTypeInquiryResponse.class)})
    @GetMapping(value = "/getboxtypebycode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getBoxTypeByCode(@RequestBody BoxTypeInquiryRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][getBoxTypeByCode] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            String boxtypeCode = EsafeboxUtils.getFieldValueAsString(BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName().getFieldName(), requestObj);
            BoxTypeDto boxType = boxTypeDslService.getBoxTypeByBoxtypeCode(logId, boxtypeCode);
            Map<String, Object> output = Maps.newHashMap();
            output.put("boxTypeInfo", boxType);
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(output)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build()));
        } catch (ESafeboxException e) {
            logger.error("[{}][getBoxTypeByCode] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createBoxType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][getBoxTypeByCode][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Create Boxtype includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = BoxTypeCrudResponse.class)})
    @PostMapping(value = "/createboxtype", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createBoxType(@RequestBody BoxTypeCreateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        request.setBoxTypeIcon(null);
        logger.info("[{}][createBoxType] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_PRICE.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_SALE.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_SET.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_ICON.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            BoxTypeDto createdBoxType = boxTypeDslService.createBoxType(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(createdBoxType)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createBoxType] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createBoxType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][createBoxType][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Update Boxtype includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = BoxTypeCrudResponse.class)})
    @PostMapping(value = "/updateboxtype", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateBoxType(@RequestBody BoxTypeUpdateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        request.setBoxTypeIcon(null);
        logger.info("[{}][updateBoxType] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(BoxTypeFieldName.BOX_TYPE_ID.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_NAME.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_PRICE_UPDATE.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_SALE_UPDATE.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_SET_UPDATE.getEsafeboxFieldName(),
                            BoxTypeFieldName.BOX_TYPE_ICON.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            BoxTypeDto updatedBoxType = boxTypeDslService.updateBoxType(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(updatedBoxType)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createBoxType] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][updateBoxType] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][updateBoxType][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }
}
