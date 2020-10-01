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
import com.ecpay.esafebox.controller.model.userbox.ActiveUserBoxRequest;
import com.ecpay.esafebox.controller.model.userbox.UserBoxListRequest;
import com.ecpay.esafebox.controller.model.userbox.UserBoxListResponse;
import com.ecpay.esafebox.controller.model.userbox.ActiveUserBoxCrudResponse.ActiveUserBoxCrudResponseData;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UserBoxDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxTypeFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UserBoxFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.TransactionDslService;
import com.ecpay.esafebox.service.UserBoxDslService;
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

@RequiredArgsConstructor
@RestController
public class BoxItemController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);

	@Autowired
	UserBoxDslService userBoxDslService;
	
	@Autowired
	TransactionDslService transactionDslService;

	@ApiOperation(value = "Return List Items in Box")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = UserBoxListResponse.class) })
	@GetMapping(value = "/listitemsbox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listItemsBox(@RequestBody UserBoxListRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][listItemsBox] Received request: {}", logId, gson.toJson(requestObj));
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validateForSearch(UserBoxFieldName.getSearchFieldNames(), requestObj);
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
							: Arrays.asList(UserBoxFieldName.USERBOX_ID.getEsafeboxFieldName().getFieldName());

			List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy,
					UserBoxFieldName.getListBoxSortFieldNames());
			if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
				responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(
						EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
						Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
								String.join(", ",
										Arrays.asList(UserBoxFieldName.USERBOX_ID.getEsafeboxFieldName().getFieldName(),
												UserBoxFieldName.USER_ID.getEsafeboxFieldName().getFieldName(),
												UserBoxFieldName.BOX_SERIAL.getEsafeboxFieldName().getFieldName(),
												UserBoxFieldName.BOX_TYPE.getEsafeboxFieldName().getFieldName(),
												UserBoxFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName()))));
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
			PagedData<UserBoxDto> pagedData = userBoxDslService.getListUserBox(logId, requestObj, pageable);
			pagedData.setSorts(sortBy);
			pagedData.setOrder(directionValue);
			if (CollectionUtils.isEmpty(pagedData.getData())) {
				responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.NO_DATA_FOUND.getErrorCode())
						.responseMessage(EsafeboxErrorCode.NO_DATA_FOUND.getDescription())
						.responseData(Collections.emptyMap()).responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(pagedData)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][listItemsBox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][listItemsBox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][listItemsBox][Duration: {}] Return response: {}", logId, endTime - logId,
					gson.toJson(response));
		}
		return response;
	}
	
	@ApiOperation(value = "Verify Items in Box")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/verifyitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> verifyItems(@RequestBody ActiveUserBoxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][verifyItems] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(UserBoxFieldName.USER_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			Map<String, Object> result = transactionDslService.esafeboEdongInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][verifyItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][verifyItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][verifyItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Get encrypted Item data in Box")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/encitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> encItems(@RequestBody ActiveUserBoxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][encItems] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(UserBoxFieldName.USER_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][encItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][encItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][encItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	
	@ApiOperation(value = "Insert Item(s) into Esafebox")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/insertitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertItems(@RequestBody ActiveUserBoxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][insertItems] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(UserBoxFieldName.USER_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][insertItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][insertItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][insertItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Update Item(s), change item's owner from one Esafebox to the other")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/updateitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateItems(@RequestBody ActiveUserBoxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][updateItems] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(UserBoxFieldName.USER_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][updateItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][updateItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][updateItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Exchange Item(s), delete and insert more item of one Esafebox")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/exchangitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> exchangItems(@RequestBody ActiveUserBoxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][exchangItems] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(UserBoxFieldName.USER_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					UserBoxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					BoxTypeFieldName.BOX_TYPE_CODE.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][exchangItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][exchangItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][exchangItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
}
