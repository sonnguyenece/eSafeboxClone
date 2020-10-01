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
import com.ecpay.esafebox.controller.model.userpair.ListUserPairRequest;
import com.ecpay.esafebox.controller.model.userpair.ListUserPairResponse;
import com.ecpay.esafebox.controller.model.userpair.UserPairCreateRequest;
import com.ecpay.esafebox.controller.model.userpair.UserPairCrudResponse;
import com.ecpay.esafebox.controller.model.userpair.UserPairUpdateRequest;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UserPairDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UserPairFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.UserPairDslService;
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
public class UserPairController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
	@Autowired
	UserPairDslService userPairDslService;

	@ApiOperation(value = "Return List Uom Type By Paging")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ListUserPairResponse.class) })
	@GetMapping(value = "/listpairuser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listUserPair(@RequestBody ListUserPairRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][listUserPair] Received request: {}", logId, gson.toJson(requestObj));
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validateForSearch(UserPairFieldName.getSearchFieldNames(), requestObj);
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
							: Arrays.asList(UserPairFieldName.USER_PAIR_ID.getEsafeboxFieldName().getFieldName());

			List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy,
					UserPairFieldName.getListUomSortFieldNames());
			if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
				responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(
						EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
						Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
								String.join(", ", Arrays.asList(
										UserPairFieldName.USER_PAIR_USER_FROM.getEsafeboxFieldName().getFieldName(),
										UserPairFieldName.USER_PAIR_USER_TO.getEsafeboxFieldName().getFieldName(),
										UserPairFieldName.USER_PAIR_STATUS.getEsafeboxFieldName().getFieldName()))));
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
			PagedData<UserPairDto> pagedData = userPairDslService.getListPairUser(logId, requestObj, pageable);
			pagedData.setSorts(sortBy);
			pagedData.setOrder(directionValue);
			if (CollectionUtils.isEmpty(pagedData.getData())) {
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(
						ResponseMessage.builder().responseCode(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getErrorCode())
								.responseMessage(EsafeboxErrorCode.NO_SEARCH_DATA_FOUND.getDescription())
								.responseData(pagedData).responseTime(System.currentTimeMillis() - logId).build()));
				return response;
			}
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(pagedData)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][listUserPair] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][listUserPair][Duration: {}] Return response: {}", logId, endTime - logId,
					gson.toJson(response));
		}
		return response;
	}

	@ApiOperation(value = "Create UserPair includes Its attributes")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = UserPairCrudResponse.class) })
	@PostMapping(value = "/adduserpair", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUserPair(@RequestBody UserPairCreateRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][createUserPair] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData
					.validate(Arrays.asList(UserPairFieldName.USER_PAIR_USER_FROM.getEsafeboxFieldName(),
							UserPairFieldName.USER_PAIR_USER_TO.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			UserPairDto createdUserPair = userPairDslService.addUserPair(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(createdUserPair)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][createUserPair] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][createUserPair] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][createUserPair][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}

	@ApiOperation(value = "unpair user")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = UserPairCrudResponse.class) })
	@PostMapping(value = "/unpairuser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateUserPair(@RequestBody UserPairUpdateRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][updateUserPair] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(UserPairFieldName.USER_PAIR_USER_FROM.getEsafeboxFieldName(),
					UserPairFieldName.USER_PAIR_USER_TO_LIST.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			List<UserPairDto> updatedUserPair = userPairDslService.unpairUser(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(updatedUserPair)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][updateUserPair] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][updateUserPair] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][updateUserPair][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
}
