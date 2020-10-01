package com.ecpay.esafebox.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecpay.entities.common.EcConstants;
import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.box.BoxCreateRequest;
import com.ecpay.esafebox.controller.model.box.BoxCrudResponse;
import com.ecpay.esafebox.controller.model.manufacture.GetManufactureDetailRequest;
import com.ecpay.esafebox.controller.model.manufacture.GetManufactureDetailResponse;
import com.ecpay.esafebox.controller.model.manufacture.ListManufactureRequest;
import com.ecpay.esafebox.controller.model.manufacture.ListManufactureResponse;
import com.ecpay.esafebox.dto.ManufactedBoxDto;
import com.ecpay.esafebox.dto.ManufactureDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.ManufactureFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.ManufactureListFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.repository.BoxDao;
import com.ecpay.esafebox.service.BoxDslService;
import com.ecpay.esafebox.service.BoxtypeDslService;
import com.ecpay.esafebox.service.ManufactureDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PaginationUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ManufactureController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);

	@Autowired
	private ManufactureDslService manufactureDslService;

	@Autowired
	BoxtypeDslService boxtypeDslService;

	@Autowired
	BoxDslService boxDslService;
	@Autowired
	BoxDao boxDaoService;

	@Transactional(rollbackFor = { Exception.class })
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = BoxCrudResponse.class) })
	@PostMapping(value = "/requestbox")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> RequestBox(@RequestBody BoxCreateRequest request) throws Exception {

		Long beginTime = System.currentTimeMillis();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> bodyRequest = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][RequestBox] Received request: {}", logId, request);

		// Check validate data
		ResponseMessage responseMessage = ValidatorData
				.validate(Arrays.asList(ManufactureFieldName.MANUFACTURE_CODE.getEsafeboxFieldName(),
						ManufactureFieldName.MANUFACTURE_BOXTYPE.getEsafeboxFieldName(),
						ManufactureFieldName.MANUFACTURE_QUANTITY.getEsafeboxFieldName()), bodyRequest);
		if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
			responseMessage.setResponseTime(System.currentTimeMillis() - beginTime);
			return ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		}

		String manufactureCode = EsafeboxUtils.getFieldValueAsString(
				ManufactureFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName(), bodyRequest).trim().toUpperCase();
		Long boxtypeId = EsafeboxUtils.getFieldValueAsLong(
				ManufactureFieldName.MANUFACTURE_BOXTYPE.getEsafeboxFieldName().getFieldName(), bodyRequest);
		Long quantityBox = EsafeboxUtils.getFieldValueAsLong(
				ManufactureFieldName.MANUFACTURE_QUANTITY.getEsafeboxFieldName().getFieldName(), bodyRequest);



		Map<String, Object> input = new HashMap<>();
		input.put("p_manu_code", manufactureCode);
		input.put("p_box_type_id", boxtypeId);
		input.put("p_quantity", quantityBox);
		Map<String, Object> output = boxDaoService.createBoxes(logId, input);
		if(Objects.isNull(output)||!output.get("err_code").equals(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())) {
			if(output.get("err_code").equals(EsafeboxErrorCode.MANUFACTURE_CODE_ALREADY_EXISTS.getErrorCode())) {
				return ResponseEntity.ok(EsafeboxUtils.buildMapStandardErrorResponse(EsafeboxErrorCode.MANUFACTURE_CODE_ALREADY_EXISTS,
						Arrays.asList(manufactureCode), System.currentTimeMillis() - beginTime));
			}
			if(output.get("err_code").equals(EsafeboxErrorCode.BOXTYPE_NOT_EXISTS.getErrorCode())) {
				return ResponseEntity.ok(EsafeboxUtils.buildMapStandardErrorResponse(EsafeboxErrorCode.BOXTYPE_NOT_EXISTS,
						Arrays.asList(manufactureCode), System.currentTimeMillis() - beginTime));
			}
			return ResponseEntity.ok(EsafeboxUtils.buildMapStandardErrorResponse(EsafeboxErrorCode.SYSTEM_ERROR,
					Arrays.asList(String.valueOf(boxtypeId)), System.currentTimeMillis() - beginTime));
		}

		responseMessage.setResponseCode(EcConstants.RESPONSE_CODE_SUCCESS_VALUE);
		responseMessage.setResponseMessage(EcConstants.RESPONSE_MESSAGE_SUCCESS_VALUE);

		Map<String, Object> responseMap = Maps.newHashMap();
		responseMap.put("manufactureId", output.get("o_manu_id"));
		responseMap.put(ManufactureFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName(),
				manufactureCode);
		responseMap.put(ManufactureFieldName.MANUFACTURE_BOXTYPE.getEsafeboxFieldName().getFieldName(),
				boxtypeId);
		responseMap.put("factureStatus", "P");

		responseMessage.setResponseData(responseMap);

		responseMessage.setResponseTime(System.currentTimeMillis() - beginTime);

		return ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
	}

	@ApiOperation(value = "Return List Manufacture By Paging")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ListManufactureResponse.class) })
	@GetMapping(value = "/listmanufacture", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listManufacture(@RequestBody ListManufactureRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][listManufacture] Received request: {}", logId, gson.toJson(requestObj));
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validateForSearch(ManufactureListFieldName.getSearchFieldNames(),
					requestObj);
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
							: Arrays.asList(
									ManufactureListFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName());

			List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy,
					ManufactureListFieldName.getSortFieldNames());
			if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
				responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(
						EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
						Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
								String.join(", ", Arrays.asList(
										ManufactureListFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName(),
										ManufactureListFieldName.MANUFACTURE_BOXTYPE.getEsafeboxFieldName()
												.getFieldName(),
										ManufactureListFieldName.FACTURE_STATUS.getEsafeboxFieldName().getFieldName(),
										ManufactureListFieldName.CREATED_DATE.getEsafeboxFieldName().getFieldName()))));
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
			PagedData<ManufactureDto> pagedData = manufactureDslService.searchManufacture(logId, requestObj, pageable);
			pagedData.setSorts(sortBy);
			pagedData.setOrder(directionValue);
			if (CollectionUtils.isEmpty(pagedData.getData())) {
				responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
						.responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
						.responseData(Collections.emptyMap()).responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(pagedData)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][listManufacture] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][listManufacture] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][listManufacture][Duration: {}] Return response: {}", logId, endTime - logId,
					gson.toJson(response));
		}
		return response;
	}
	
	@Transactional(rollbackFor = { Exception.class })
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = GetManufactureDetailResponse.class) })
	@GetMapping(value = "/getmanufacturedetail")
	public ResponseEntity<?> getmanufacturedetail(@RequestBody GetManufactureDetailRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][listManufacture] Received request: {}", logId, gson.toJson(requestObj));
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validateForSearch(Arrays.asList(
					ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName(),
					PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
					PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName()),
					requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}

			Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(
					PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);
//
			@SuppressWarnings("unchecked")
			List<String> sortBy = (sortsObject != null && sortsObject instanceof List
					&& !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
							: Arrays.asList(
									ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName().getFieldName());
			List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy,
					Arrays.asList(ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName()));
			String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue()
					.toString();
			Pageable pageable = PaginationUtils.buildPageable(requestObj, sortByEntityFieldName, defaultDirectionValue);

			Long manufactureId = EsafeboxUtils.getFieldValueAsLong(
					ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName().getFieldName(), requestObj);

			// check param manufactureId
            if(Objects.isNull(manufactureId)){
                responseMessage = ResponseMessage.builder()
                        .responseCode(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode())
                        .responseMessage(
                                EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(ManufactureFieldName.MANUFACTURE_ID.getEsafeboxFieldName().getFieldName()))
                        .responseData(Collections.emptyMap()).responseTime(System.currentTimeMillis() - logId).build();
                response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
                return response;
            }
            
            ManufactureDto manufacture = manufactureDslService.getManufacturebyId(manufactureId);
            PagedData<ManufactedBoxDto> boxPage = manufactureDslService.getManufactedBox(logId, requestObj, pageable);

            if (Objects.isNull(manufacture)) {
				responseMessage = ResponseMessage.builder()
						.responseCode(EsafeboxErrorCode.MANUFACTURE_NOT_EXISTS.getErrorCode())
						.responseMessage(
								EsafeboxErrorCode.MANUFACTURE_NOT_EXISTS.getDescription(String.valueOf(manufactureId)))
						.responseData(Collections.emptyMap()).responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			
			Map<String, Object> responseMap = Maps.newHashMap();
			responseMap.put("manufactureDetail", manufacture);
			responseMap.put("listBox", boxPage);
			
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(responseMap)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][getManufactureDetail] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][getManufactureDetail][Duration: {}] Return response: {}", logId, endTime - logId,
					gson.toJson(response));
		}
		return response;
	}
}