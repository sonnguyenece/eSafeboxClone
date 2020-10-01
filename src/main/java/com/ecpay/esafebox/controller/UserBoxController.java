package com.ecpay.esafebox.controller;

import java.util.Arrays;
import java.util.Collection;
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
import com.ecpay.esafebox.controller.model.userbox.ActiveUserBoxCrudResponse.ActiveUserBoxCrudResponseData;
import com.ecpay.esafebox.controller.model.userbox.ActiveUserBoxRequest;
import com.ecpay.esafebox.controller.model.userbox.UserBoxKeyUpdateRequest;
import com.ecpay.esafebox.controller.model.userbox.UserBoxListRequest;
import com.ecpay.esafebox.controller.model.userbox.UserBoxListResponse;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.UserBoxDto;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxTypeFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.UserBoxFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.UserBoxDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PaginationUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@Api(value = "API(s) for Box Type", description = "Operations maintaining the User's Box")
public class UserBoxController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);

	@Autowired
	UserBoxDslService userBoxDslService;

	@ApiOperation(value = "Return List User's Box By Paging")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = UserBoxListResponse.class) })
	@GetMapping(value = "/listuserbox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listUserBox(@RequestBody UserBoxListRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][listUserBox] Received request: {}", logId, gson.toJson(requestObj));
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
			logger.error("[{}][listUserBox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][listUserBox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][listUserBox][Duration: {}] Return response: {}", logId, endTime - logId,
					gson.toJson(response));
		}
		return response;
	}

	@ApiOperation(value = "activate UserBox")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/activeuserbox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> activeUserBox(@RequestBody ActiveUserBoxRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][activeUserBox] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		Map<String, Object> responseData = Maps.newHashMap();
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
			List<UserBoxDto> userBoxes = userBoxDslService.activeUserBox(logId, requestObj);
			responseData.put("listUserBox", userBoxes);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(responseData)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][activeUserBox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][activeUserBox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][activeUserBox][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
	
	@ApiOperation(value = "Update Alias, Key, Algorithm for UserBox")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ActiveUserBoxCrudResponseData.class) })
	@PostMapping(value = "/updateboxkey", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateBoxKey(@RequestBody UserBoxKeyUpdateRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String, Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
		});
		logger.info("[{}][updateBoxKey] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validate(Arrays.asList(
					UserBoxFieldName.USER_ID.getEsafeboxFieldName(),
					UserBoxFieldName.BOX_ID.getEsafeboxFieldName(),
					UserBoxFieldName.USERBOX_ALIAS.getEsafeboxFieldName(),
					UserBoxFieldName.USERBOX_KP.getEsafeboxFieldName(),
					UserBoxFieldName.USERBOX_ALGORITHM.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			UserBoxDto userBox = userBoxDslService.updateBoxKey(logId, requestObj);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription()).responseData(userBox)
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][updateBoxKey] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder().responseCode(e.getMessage()).responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][updateBoxKey] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder().responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][updateBoxKey][Duration: {}] Return response: {}", logId, endTime - logId,
					responseMessage);
		}
		return response;
	}
}
