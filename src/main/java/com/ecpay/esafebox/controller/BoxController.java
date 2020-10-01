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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.box.ListBoxRequest;
import com.ecpay.esafebox.controller.model.box.ListBoxResponse;
import com.ecpay.esafebox.dto.BoxDto;
import com.ecpay.esafebox.dto.PagedData;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.BoxFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.service.BoxDslService;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PaginationUtils;
import com.ecpay.esafebox.validation.ValidatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RequiredArgsConstructor
@RestController
@Api(value = "API(s) for Box", description = "Operations maintaining the Box")
public class BoxController {
	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.API);
	
	@Autowired
	BoxDslService boxDslService;
	
	@ApiOperation(value = "Return List Box By Paging")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK", response = ListBoxResponse.class) })
	@GetMapping(value = "/listbox", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listBox(@RequestBody ListBoxRequest request) throws Exception {
		Gson gson = new Gson();
		long logId = System.currentTimeMillis();
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(request);
		Map<String,Object> requestObj = objectMapper.readValue(jsonString, new TypeReference<Map<String,Object>>() {});
		logger.info("[{}][listBox] Received request: {}", logId, gson.toJson(requestObj));
		ResponseMessage responseMessage = new ResponseMessage();
		ResponseEntity<?> response = null;
		// additional request validation here
		try {
			responseMessage = ValidatorData.validateForSearch(BoxFieldName.getSearchFieldNames(), requestObj);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				responseMessage.setResponseTime(System.currentTimeMillis() - logId);
				response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
				return response;
			}
			
			Object sortsObject = EsafeboxUtils.getFieldValueAsArrayOfString(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(), requestObj);
			
			@SuppressWarnings("unchecked")
			List<String> sortBy = (sortsObject != null && sortsObject instanceof List && !((List<String>) sortsObject).isEmpty()) ? (List<String>) sortsObject
					: Arrays.asList(BoxFieldName.BOX_ID.getEsafeboxFieldName().getFieldName());
			
			List<String> sortByEntityFieldName = PaginationUtils.getEntitySortFieldNames(sortBy, BoxFieldName.getListBoxSortFieldNames());
			if (Objects.isNull(sortByEntityFieldName) || sortByEntityFieldName.isEmpty()) {
				responseMessage = EsafeboxUtils.buildObjectStandardErrorResponse(EsafeboxErrorCode.SORTS_PARAM_IS_INVALID,
						Arrays.asList(PaginationSortingFieldName.SORTS.getEsafeboxFieldName().getFieldName(),
								String.join(", ", Arrays.asList(BoxFieldName.BOX_ID.getEsafeboxFieldName().getFieldName(),
										BoxFieldName.BOX_TYPE.getEsafeboxFieldName().getFieldName(),
										BoxFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName(),
										BoxFieldName.BOX_SERIAL.getEsafeboxFieldName().getFieldName(),
										BoxFieldName.BOX_STATUS.getEsafeboxFieldName().getFieldName(),
										BoxFieldName.CREATED_DATE.getEsafeboxFieldName().getFieldName()))));
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
			PagedData<BoxDto> boxPagedData = boxDslService.getListBox(logId, requestObj, pageable);
			boxPagedData.setSorts(sortBy);
			boxPagedData.setOrder(directionValue);
			if (CollectionUtils.isEmpty(boxPagedData.getData())) {
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
					.responseData(boxPagedData)
					.responseTime(System.currentTimeMillis() - logId)
					.build();
			response = ResponseEntity.ok(EsafeboxUtils.buildStandardResponse(responseMessage));
		} catch (ESafeboxException e) {
			logger.error("[{}][listBox] EsafeboxException: {}: {}", logId, e.getMessage(), e.getReason());
			responseMessage = ResponseMessage.builder()
					.responseCode(e.getMessage())
					.responseMessage(e.getReason())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils
					.buildStandardResponse(responseMessage));
		} catch (Exception e) {
			logger.error("[{}][listBox] Exception: {}", logId, e.getMessage(), e);
			responseMessage = ResponseMessage.builder()
					.responseCode(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode())
					.responseMessage(EsafeboxErrorCode.SYSTEM_ERROR.getDescription())
					.responseTime(System.currentTimeMillis() - logId).build();
			response = ResponseEntity.ok(EsafeboxUtils
					.buildStandardResponse(responseMessage));
		} finally {
			long endTime = System.currentTimeMillis();
			responseMessage.setResponseTime(endTime - logId);
			logger.info("[{}][listBox][Duration: {}] Return response: {}", logId, endTime - logId, responseMessage);
		}
		return response;
	}
}
