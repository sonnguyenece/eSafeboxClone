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
import com.ecpay.esafebox.controller.model.uom.ListUomRequest;
import com.ecpay.esafebox.controller.model.uom.ListUomResponse;
import com.ecpay.esafebox.controller.model.uom.UomCreateRequest;
import com.ecpay.esafebox.controller.model.uom.UomCrudResponse;
import com.ecpay.esafebox.controller.model.uom.UomUpdateRequest;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UomDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UomFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.UomDslService;
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
public class UomController {
    private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
    @Autowired
    UomDslService uomDslService;

    @ApiOperation(value = "Return List Uom By Paging")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = ListUomResponse.class)})
    @GetMapping(value = "/listuom", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listUom(@RequestBody ListUomRequest request) throws Exception {
        Gson gson = new Gson();
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][listUom] Received request: {}", logId, gson.toJson(requestObj));
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData.validateForSearch(UomFieldName.getSearchFieldNames(), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }

            Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);

            @SuppressWarnings("unchecked")
            List<String> sortBy = (sortsObject != null && sortsObject instanceof List && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
                    : Arrays.asList(UomFieldName.UOM_ID.getEsafeboxFieldName().getFieldName());

            List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy, UomFieldName.getListUomSortFieldNames());
            if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
                responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
                        Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
                                String.join(", ", Arrays.asList(UomFieldName.UOM_ID.getEsafeboxFieldName().getFieldName(),
                                        UomFieldName.UOM_TYPE_ID.getEsafeboxFieldName().getFieldName(),
                                        UomFieldName.UOM_ABBREVIATION.getEsafeboxFieldName().getFieldName(),
                                        UomFieldName.UOM_NAME.getEsafeboxFieldName().getFieldName()))));
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
            PagedData<UomDto> pagedData = uomDslService.getListUom(logId, requestObj, pageable);
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
            logger.error("[{}][listUom] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][listUom] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][listUom][Duration: {}] Return response: {}", logId, endTime - logId, gson.toJson(response));
        }
        return response;
    }

    @ApiOperation(value = "Create Uom includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = UomCrudResponse.class)})
    @PostMapping(value = "/createuom", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUom(@RequestBody UomCreateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][createUom] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(UomFieldName.UOM_TYPE_ID.getEsafeboxFieldName(),
                            UomFieldName.UOM_ABBREVIATION.getEsafeboxFieldName(),
                            UomFieldName.UOM_NAME.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            UomDto createdUom = uomDslService.createUom(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(createdUom)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][createUom] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][createUom] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][createUom][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }

    @ApiOperation(value = "Update Uom includes Its attributes")
    @ApiResponses({@ApiResponse(code = 200, message = "OK", response = UomCrudResponse.class)})
    @PostMapping(value = "/updateuom", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUom(@RequestBody UomUpdateRequest request) throws Exception {
        long logId = System.currentTimeMillis();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(request);
        Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
        logger.info("[{}][updateUom] Received request: {}", logId, request);
        ResponseMessage responseMessage = new ResponseMessage();
        ResponseEntity<?> response = null;
        // additional request validation here
        try {
            responseMessage = ValidatorData
                    .validate(Arrays.asList(UomFieldName.UOM_ID.getEsafeboxFieldName(),
                            UomFieldName.UOM_TYPE_ID.getEsafeboxFieldName(),
                            UomFieldName.UOM_ABBREVIATION.getEsafeboxFieldName(),
                            UomFieldName.UOM_NAME.getEsafeboxFieldName()), requestObj);
            if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
                responseMessage.setResponseTime(System.currentTimeMillis() - logId);
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            UomDto updatedUom = uomDslService.updateUom(logId, requestObj);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
                    .responseData(updatedUom)
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
        } catch (ESafeboxException e) {
            logger.error("[{}][updateUom] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
            responseMessage = ResponseMessage.builder()
                    .responseCode(e.getMessage())
                    .responseMessage(e.getReason())
                    .responseTime(System.currentTimeMillis() - logId)
                    .build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } catch (Exception e) {
            logger.error("[{}][updateUom] Exception: {}", logId, e.getMessage(), e);
            responseMessage = ResponseMessage.builder()
                    .responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
                    .responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
                    .responseTime(System.currentTimeMillis() - logId).build();
            response = ResponseEntity.ok(EsafeboxUtils
                    .buildStandardResponse(responseMessage));
        } finally {
            long endTime = System.currentTimeMillis();
            responseMessage.setResponseTime(endTime - logId);
            logger.info("[{}][updateUom][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
        }
        return response;
    }
}
