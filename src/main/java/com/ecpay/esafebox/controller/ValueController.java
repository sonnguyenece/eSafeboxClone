package com.ecpay.esafebox.controller;

import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.value.CreateBoxTypeValueRequest;
import com.ecpay.esafebox.controller.model.value.CreateBoxTypeValueResponse;
import com.ecpay.esafebox.dto.ValueDtos;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.ValueFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.ValueDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ValueController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
	
	@Autowired
	ValueDslService valueDslService;
	
	@ApiOperation(value = "Create Boxtype includes Its attributes")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = CreateBoxTypeValueResponse.class) })
	@PostMapping(value = "/createboxtypevalue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createBoxTypeValue(@RequestBody CreateBoxTypeValueRequest request) throws Exception {
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String,Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String,Object>>() {});
		logger.info("[{}][createBoxTypeValue] Received request: {}", logId, request);
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData
					.validate(Arrays.asList(ValueFieldName.BOX_TYPE_ID.getEsafeboxFieldName()), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			ValueDtos createdBoxType = valueDslService.createBoxTypeValue(logId, requestObj);
			responseMessage = ResponseMessage.builder()
					.responseCode(EsafeboxErrorCode.SUCCESSFUL.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SUCCESSFUL.getDescription())
					.responseData(createdBoxType)
					.responseTime(System.currentTimeMillis() - logId)
					.build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][createBoxTypeValue] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder()
					.responseCode(e.getMessage())
					.responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId)
					.build();
			response = ResponseEntity.ok(EsafeboxUtils
					.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][createBoxTypeValue] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder()
					.responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils
					.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][createBoxTypeValue][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
		}
		return response;
	}
}
