package com.ecpay.esafebox.controller;

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
import com.ecpay.esafebox.controller.model.transaction.EdongEsafeboxRequest;
import com.ecpay.esafebox.controller.model.transaction.EdongEsafeboxResponse;
import com.ecpay.esafebox.controller.model.transaction.EsafeboxEdongRequest;
import com.ecpay.esafebox.controller.model.transaction.EsafeboxEdongResponse;
import com.ecpay.esafebox.controller.model.transaction.ExchangeEsafeboxRequest;
import com.ecpay.esafebox.controller.model.transaction.ExchangeEsafeboxResponse;
import com.ecpay.esafebox.controller.model.transaction.ListBoxTransactionRequest;
import com.ecpay.esafebox.controller.model.transaction.ListBoxTransactionResponse;
import com.ecpay.esafebox.controller.model.transaction.TransactionInsertRequest;
import com.ecpay.esafebox.controller.model.transaction.TransactionInsertResponse;
import com.ecpay.esafebox.controller.model.transaction.TransferEsafeboxRequest;
import com.ecpay.esafebox.controller.model.transaction.TransferEsafeboxResponse;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.TransactionDto;
import com.ecpay.esafebox.dto.TransactionItem;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxTypeFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.EdongEsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.TransactionFieldName;
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
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class TransactionController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);

	@Autowired
	UserBoxDslService userBoxDslService;
	
	@Autowired
	TransactionDslService transactionDslService;

	@ApiOperation(value = "Return List Box Transaction By Paging")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ListBoxTransactionResponse.class) })
	@GetMapping(value = "/listboxtransaction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listBoxTransaction(@RequestBody ListBoxTransactionRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][listBoxTransaction] Received request: {}", logId, gson.toJson(requestObj));
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validateForSearch(TransactionFieldName.getSearchFieldNames(), requestObj);
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
							: Arrays.asList(TransactionFieldName.TRANSACTION_ID.getEsafeboxFieldName().getFieldName());

			List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy,
					TransactionFieldName.getSortFieldNames());
			if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
				responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(
						EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
						Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
								String.join(", ", Arrays.asList(
										TransactionFieldName.SENDER.getEsafeboxFieldName().getFieldName(),
										TransactionFieldName.RECEIVER.getEsafeboxFieldName().getFieldName(),
										TransactionFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName(),
										TransactionFieldName.TO_DATE.getEsafeboxFieldName().getFieldName(),
										TransactionFieldName.TRANSACTION_TYPE.getEsafeboxFieldName().getFieldName()))));
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
			PagedData<TransactionDto> pagedData = transactionDslService.getListTransaction(logId, requestObj, pageable);
			pagedData.setSorts(sortBy);
			pagedData.setOrder(directionValue);
			if (CollectionUtils.isEmpty(pagedData.getData())) {
				responseMessage = ResponseMessage.builder()
						.responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
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
			logger.error("[{}][listBoxTransaction] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][listBoxTransaction] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][listBoxTransaction][Duration: {}] Return response: {}", logId, endTime - logId,
					gson.toJson(response));
		}
		return response;
	}
	
	@ApiOperation(value = "Transfer multi currency from Edong to Esafebox")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = EdongEsafeboxResponse.class) })
	@PostMapping(value = "/edongesafebox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> eDongeSafebox(@RequestBody EdongEsafeboxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][eDongeSafebox] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.SENDER.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.KEYBOX_CLIENT.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			// additional request validation here
			List<Object> itemsObjects = EsafeboxUtils.getFieldValueAsArrayOfObject(
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName().getFieldName(), requestObj);
			String itemsJson = objectMapper.writeValueAsString(itemsObjects);
			List<TransactionItem> itemList = null;
			try {
				itemList = objectMapper.readValue(itemsJson, new TypeReference<List<TransactionItem>>() {});
			} catch (Exception e) {
				logger.info("[{}][eSafeboxeDong] Validate Items in Request Failed", logId, e);
			}
			
			if (Objects.isNull(itemList)) {
				responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
						.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
						.responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			
			for (TransactionItem transactionItem : itemList) {
				responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.ISSUER_CODE.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_VALUES.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_QUANTITIES.getEsafeboxFieldName()), EsafeboxUtils.convertObject2Map(transactionItem));
				if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
					responseMessage.setResponseTime(System.currentTimeMillis() - logId);
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
				if (transactionItem.getValues().size() != transactionItem.getQuantities().size()) {
					responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
							.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
							.responseTime(System.currentTimeMillis() - logId).build();
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][eDongeSafebox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][eDongeSafebox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][eDongeSafebox][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Transfer from Esafebox to Edong")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = EsafeboxEdongResponse.class) })
	@PostMapping(value = "/esafeboxedong", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> eSafeboxeDong(@RequestBody EsafeboxEdongRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][eSafeboxeDong] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.SENDER.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.KEYBOX_CLIENT.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			// additional request validation here
			List<Object> itemsObjects = EsafeboxUtils.getFieldValueAsArrayOfObject(
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName().getFieldName(), requestObj);
			String itemsJson = objectMapper.writeValueAsString(itemsObjects);
			List<TransactionItem> itemList = null;
			try {
				itemList = objectMapper.readValue(itemsJson, new TypeReference<List<TransactionItem>>() {});
			} catch (Exception e) {
				logger.info("[{}][eSafeboxeDong] Validate Items in Request Failed", logId, e);
			}
			
			if (Objects.isNull(itemList)) {
				responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
						.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
						.responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			
			for (TransactionItem transactionItem : itemList) {
				responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.ISSUER_CODE.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_VALUES.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_QUANTITIES.getEsafeboxFieldName()), EsafeboxUtils.convertObject2Map(transactionItem));
				if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
					responseMessage.setResponseTime(System.currentTimeMillis() - logId);
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
				if (transactionItem.getValues().size() != transactionItem.getQuantities().size()) {
					responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
							.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
							.responseTime(System.currentTimeMillis() - logId).build();
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][eSafeboxeDong] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][eSafeboxeDong] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][eSafeboxeDong][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	
	@ApiOperation(value = "Transfer from Esafebox to Esafebox")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = TransferEsafeboxResponse.class) })
	@PostMapping(value = "/transferbox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> transferBox(@RequestBody TransferEsafeboxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][transferBox] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.SENDER.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.KEYBOX_CLIENT.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			// additional request validation here
			List<Object> itemsObjects = EsafeboxUtils.getFieldValueAsArrayOfObject(
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName().getFieldName(), requestObj);
			String itemsJson = objectMapper.writeValueAsString(itemsObjects);
			List<TransactionItem> itemList = null;
			try {
				itemList = objectMapper.readValue(itemsJson, new TypeReference<List<TransactionItem>>() {});
			} catch (Exception e) {
				logger.info("[{}][transferBox] Validate Items in Request Failed", logId, e);
			}
			
			if (Objects.isNull(itemList)) {
				responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
						.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
						.responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			
			for (TransactionItem transactionItem : itemList) {
				responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.ISSUER_CODE.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_VALUES.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_QUANTITIES.getEsafeboxFieldName()), EsafeboxUtils.convertObject2Map(transactionItem));
				if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
					responseMessage.setResponseTime(System.currentTimeMillis() - logId);
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
				if (transactionItem.getValues().size() != transactionItem.getQuantities().size()) {
					responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
							.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
							.responseTime(System.currentTimeMillis() - logId).build();
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][transferBox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][transferBox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][transferBox][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	
	@ApiOperation(value = "Exchange item(s) in Esafebox to another item(s)")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ExchangeEsafeboxResponse.class) })
	@PostMapping(value = "/exchangebox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> exchangeBox(@RequestBody ExchangeEsafeboxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][exchangeBox] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.SENDER.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.RECEIVER_SERIAL.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.KEYBOX_CLIENT.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_ID.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.TERMINAL_INFO.getEsafeboxFieldName(),
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			// additional request validation here
			List<Object> itemsObjects = EsafeboxUtils.getFieldValueAsArrayOfObject(
					EdongEsafeboxFieldName.LIST_ITEMS.getEsafeboxFieldName().getFieldName(), requestObj);
			String itemsJson = objectMapper.writeValueAsString(itemsObjects);
			List<TransactionItem> itemList = null;
			try {
				itemList = objectMapper.readValue(itemsJson, new TypeReference<List<TransactionItem>>() {});
			} catch (Exception e) {
				logger.info("[{}][exchangeBox] Validate Items in Request Failed", logId, e);
			}
			
			if (Objects.isNull(itemList)) {
				responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
						.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
						.responseTime(System.currentTimeMillis() - logId).build();
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			
			for (TransactionItem transactionItem : itemList) {
				responseMessage = ValidatorData.validate(Arrays.asList(EdongEsafeboxFieldName.ISSUER_CODE.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_VALUES.getEsafeboxFieldName(),
						EdongEsafeboxFieldName.ITEM_QUANTITIES.getEsafeboxFieldName()), EsafeboxUtils.convertObject2Map(transactionItem));
				if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
					responseMessage.setResponseTime(System.currentTimeMillis() - logId);
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
				if (transactionItem.getValues().size() != transactionItem.getQuantities().size()) {
					responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.INVALID_ITEMS.getErrorCode())
							.responseMessage(EsafeboxErrorCode.INVALID_ITEMS.getDescription())
							.responseTime(System.currentTimeMillis() - logId).build();
					response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
					return response;
				}
			}
			Map<String, Object> result = transactionDslService.edongEsafeboxInitTransaction(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(result)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][exchangeBox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][exchangeBox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][exchangeBox][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	

	@ApiOperation(value = "Insert transaction")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = TransactionInsertResponse.class) })
	@PostMapping(value = "/inserttrans", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertTransaction(@RequestBody TransactionInsertRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][insertTransaction] Received request: {}", logId, request);
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
			logger.error("[{}][insertTransaction] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][insertTransaction] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][insertTransaction][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Insert transaction and insert item to complete edong to esafebox transaction")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = TransactionInsertResponse.class) })
	@PostMapping(value = "/inserttrans-and-items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertTransactionAndInsertItems(@RequestBody TransactionInsertRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][insertTransactionAndInsertItems] Received request: {}", logId, request);
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
			logger.error("[{}][insertTransactionAndInsertItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][insertTransactionAndInsertItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][insertTransactionAndInsertItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Insert transaction and update items to complete transfer box to box transaction")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = TransactionInsertResponse.class) })
	@PostMapping(value = "/inserttrans-and-updateitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertTransactionAndUpdateItems(@RequestBody TransactionInsertRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][insertTransactionAndUpdateItems] Received request: {}", logId, request);
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
			logger.error("[{}][insertTransactionAndUpdateItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][insertTransactionAndUpdateItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][insertTransactionAndUpdateItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Insert transaction and remove items to complete box to edong transaction")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = TransactionInsertResponse.class) })
	@PostMapping(value = "/inserttran-and-removeitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertTransactionAndRemoveItems(@RequestBody TransactionInsertRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][insertTransactionAndRemoveItems] Received request: {}", logId, request);
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
			logger.error("[{}][insertTransactionAndRemoveItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][insertTransactionAndRemoveItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][insertTransactionAndRemoveItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Insert transaction and exchange items (combination of delete and insert items) to complete exchange in same box transaction")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = TransactionInsertResponse.class) })
	@PostMapping(value = "/inserttran-and-exchangeitems", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> insertTransactionAndExchangeItems(@RequestBody TransactionInsertRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][insertTransactionAndExchangeItems] Received request: {}", logId, request);
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
			logger.error("[{}][insertTransactionAndExchangeItems] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][insertTransactionAndExchangeItems] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][insertTransactionAndExchangeItems][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
}
