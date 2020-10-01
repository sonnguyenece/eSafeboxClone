package com.ecpay.esafebox.validation;

import com.ecpay.entities.authority.TbChannel;
import com.ecpay.entities.common.EcConstants;
import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.PatternDetail;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ecpay.entities.common.EcConstants.*;
import static com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode.*;

@Slf4j
@UtilityClass
public class ValidatorData {

	public ResponseMessage validate(List<EsafeboxFieldName> esafeboxFieldNames, Map<String, Object> inputData) {
		String code = RESPONSE_CODE_SUCCESS_VALUE;
		String message = RESPONSE_MESSAGE_SUCCESS_VALUE;

		for (EsafeboxFieldName esafeboxFieldName : esafeboxFieldNames) {

			//Get content of field
			Object content = inputData.get(esafeboxFieldName.getFieldName());

			//Check mandatory of field
			if (esafeboxFieldName.isMandatory()
					&& StringUtils.isEmpty(content)
					&& !EsafeboxUtils.hasText(content)) {
				code = PARAM_IS_MISSED.getErrorCode();
				message = PARAM_IS_MISSED.getDescription(esafeboxFieldName.getFieldName());
				break;
			}

			//Check data type
			EsafeboxDataType dataType = esafeboxFieldName.getDataType();
			if (!StringUtils.isEmpty(content)
					&& EsafeboxUtils.hasText(content)
					&& !dataType.isValid(content)
			) {
				code = DATA_IS_INVALID_DATA_TYPE.getErrorCode();
				message = DATA_IS_INVALID_DATA_TYPE.getDescription(esafeboxFieldName.getFieldName(), dataType.name());
				break;
			}

			//Check length
			int length = esafeboxFieldName.getLength();
			if (dataType == EsafeboxDataType.STRING
					&& content != null
					&& content.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > length) {
				code = DATA_IS_INVALID_LENGTH.getErrorCode();
				message = DATA_IS_INVALID_LENGTH.getDescription(esafeboxFieldName.getFieldName(), String.valueOf(length), String.valueOf(content.toString().length()));
				break;
			}
			
			//Check minValue, maxValue
			Long minValue = esafeboxFieldName.getMinValue();
			Long maxValue = esafeboxFieldName.getMaxValue();
			if(dataType == EsafeboxDataType.LONG) {
				if (content != null && minValue != null && maxValue != null
						&& (Long.parseLong(content.toString()) > maxValue || Long.parseLong(content.toString()) < minValue)) {
					code = DATA_MAX_MIN.getErrorCode();
					message = DATA_MAX_MIN.getDescription(esafeboxFieldName.getFieldName(), content.toString(), String.valueOf(minValue), String.valueOf(maxValue));
					break;
				}
			}
			

			//Check pattern
			PatternDetail patternDetail = esafeboxFieldName.getPattern();
			if (patternDetail != null
					&& !StringUtils.isEmpty(content)
					&& esafeboxFieldName.getDataType() != EsafeboxDataType.DATE) {
				if (!content.toString().matches(patternDetail.getPattern())) {
					code = DATA_IS_INVALID_PATTERN.getErrorCode();
					message = String.format(patternDetail.getErrorMessage(), content);
					break;
				}
			}

			if (patternDetail != null
					&& !StringUtils.isEmpty(content)
					&& esafeboxFieldName.getDataType() == EsafeboxDataType.DATE) {
				try {
					DATE_FORMATTER.parse(content.toString());
				} catch (DateTimeParseException dtpe) {
					code = DATA_IS_INVALID_PATTERN.getErrorCode();
					message = patternDetail.getErrorMessage();
					break;
				}
			}


			//Check fixed values for single value
			List<String> fixedValues = esafeboxFieldName.getFixedValues();
			if (esafeboxFieldName.getDataType() != EsafeboxDataType.ARRAY
					&& esafeboxFieldName.getDataType() != EsafeboxDataType.ARRAY_OF_STRING
					&& esafeboxFieldName.getDataType() != EsafeboxDataType.ARRAY_OF_LONG
					&& !CollectionUtils.isEmpty(fixedValues)
					&& content != null
					&& !fixedValues.contains(content.toString())) {
				code = DATA_IS_INVALID_FIXED_VALUES.getErrorCode();
				message = DATA_IS_INVALID_FIXED_VALUES.getDescription(esafeboxFieldName.getFieldName(), fixedValues.toString(), content.toString());
				break;
			}

			//Check fixed values for simple array
			if ((esafeboxFieldName.getDataType() == EsafeboxDataType.ARRAY
					|| esafeboxFieldName.getDataType() == EsafeboxDataType.ARRAY_OF_LONG
					|| esafeboxFieldName.getDataType() == EsafeboxDataType.ARRAY_OF_STRING)
					&& !CollectionUtils.isEmpty(fixedValues)
					&& content != null) {
				List<String> itemValues = (List<String>) inputData.get(esafeboxFieldName.getFieldName());
				for (String itemValue : itemValues) {
					if (!fixedValues.contains(itemValue)) {
						code = DATA_IS_INVALID_FIXED_VALUES.getErrorCode();
						message = DATA_IS_INVALID_FIXED_VALUES.getDescription(esafeboxFieldName.getFieldName(), fixedValues.toString(), content.toString());
						break;
					}
				}
				break;
			}

		}

		return ResponseMessage.builder()
				.responseCode(code)
				.responseMessage(message)
				.build();
	}

	public ResponseMessage validateForSearch(List<EsafeboxFieldName> esafeboxFieldNames, Map<String, Object> inputData) {
		String code = RESPONSE_CODE_SUCCESS_VALUE;
		String message = RESPONSE_MESSAGE_SUCCESS_VALUE;

		for (EsafeboxFieldName esafeboxFieldName : esafeboxFieldNames) {

			//Get content of field
			Object content = inputData.get(esafeboxFieldName.getFieldName());

			//Check data type
			EsafeboxDataType dataType = esafeboxFieldName.getDataType();
			if (!StringUtils.isEmpty(content)
					&& EsafeboxUtils.hasText(content)
					&& !dataType.isValid(content)) {
				code = DATA_IS_INVALID_DATA_TYPE.getErrorCode();
				message = DATA_IS_INVALID_DATA_TYPE.getDescription(esafeboxFieldName.getFieldName(), dataType.name());
				break;
			}

			//Check pattern
			PatternDetail patternDetail = esafeboxFieldName.getPattern();
			if (patternDetail != null
					&& !StringUtils.isEmpty(content)
					&& esafeboxFieldName.getDataType() != EsafeboxDataType.DATE) {
				if (!content.toString().matches(patternDetail.getPattern())) {
					code = DATA_IS_INVALID_PATTERN.getErrorCode();
					message = patternDetail.getErrorMessage();
					break;
				}
			}

			if (patternDetail != null
					&& !StringUtils.isEmpty(content)
					&& esafeboxFieldName.getDataType() == EsafeboxDataType.DATE) {
				try {
					DATE_FORMATTER.parse(content.toString());
				} catch (DateTimeParseException dtpe) {
					code = DATA_IS_INVALID_PATTERN.getErrorCode();
					message = patternDetail.getErrorMessage();
					break;
				}
			}

			//Check fixed values for single value
			List<String> fixedValues = esafeboxFieldName.getFixedValues();
			if (esafeboxFieldName.getDataType() != EsafeboxDataType.ARRAY
					&& esafeboxFieldName.getDataType() != EsafeboxDataType.ARRAY_OF_LONG
					&& esafeboxFieldName.getDataType() != EsafeboxDataType.ARRAY_OF_STRING
					&& !StringUtils.isEmpty(content)
					&& EsafeboxUtils.hasText(content)
					&& !CollectionUtils.isEmpty(fixedValues)
					&& !fixedValues.contains(content.toString())) {
				code = DATA_IS_INVALID_FIXED_VALUES.getErrorCode();
				message = DATA_IS_INVALID_FIXED_VALUES.getDescription(esafeboxFieldName.getFieldName(), fixedValues.toString(), content.toString());
				break;
			}

			//Check fixed values for simple array
			if ((esafeboxFieldName.getDataType() == EsafeboxDataType.ARRAY
						|| esafeboxFieldName.getDataType() == EsafeboxDataType.ARRAY_OF_STRING
						|| esafeboxFieldName.getDataType() == EsafeboxDataType.ARRAY_OF_LONG)
					&& !CollectionUtils.isEmpty(fixedValues)
					&& inputData.get(esafeboxFieldName.getFieldName()) != null) {
				List<String> itemValues = (List<String>) inputData.get(esafeboxFieldName.getFieldName());
				for (String itemValue : itemValues) {
					if (!fixedValues.contains(itemValue)) {
						code = DATA_IS_INVALID_FIXED_VALUES.getErrorCode();
						message = DATA_IS_INVALID_FIXED_VALUES.getDescription(esafeboxFieldName.getFieldName(), fixedValues.toString(), content.toString());
						break;
					}
				}
				break;
			}

		}

		return ResponseMessage.builder()
				.responseCode(code)
				.responseMessage(message)
				.build();
	}

	public ResponseMessage validateIds(String fieldName, List<Object> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return ResponseMessage.builder()
					.responseCode(PARAM_IS_MISSED.getErrorCode())
					.responseMessage(PARAM_IS_MISSED.getDescription(fieldName))
					.build();
		}

		String code = RESPONSE_CODE_SUCCESS_VALUE;
		String message = RESPONSE_MESSAGE_SUCCESS_VALUE;
		for (Object id : ids) {
			if (id == null) {
				code = PARAM_IS_MISSED.getErrorCode();
				message = PARAM_IS_MISSED.getDescription("id");
				break;
			}

			if (!(id instanceof Long) && !(id instanceof Integer)) {
				code = DATA_IS_INVALID_DATA_TYPE.getErrorCode();
				message = DATA_IS_INVALID_DATA_TYPE.getDescription("id", "NUMERIC");
				break;
			}
		}

		return ResponseMessage.builder()
				.responseCode(code)
				.responseMessage(message)
				.build();

	}

	public ResponseMessage validateListObjects(String fieldName, List<EsafeboxFieldName> esafeboxFieldNames,
											   List<Object> listObjectData, Map<String, List<String>> uniqueFieldNameMap) {
		if (CollectionUtils.isEmpty(listObjectData)) {
			return ResponseMessage.builder()
					.responseCode(PARAM_IS_MISSED.getErrorCode())
					.responseMessage(PARAM_IS_MISSED.getDescription(fieldName))
					.build();
		}

		Map<String, Set<String>> uniqueFieldValueMap = Maps.newHashMap();
		for (Object obj : listObjectData) {
			if (obj == null) {
				log.warn("Item data is null. Please consider the input data with LIST.");
				continue;
			}

			//Convert item object to map data
			Map<String, Object> itemDataMap = EsafeboxUtils.convertObject2Map(obj);

			//Get list unique fields value
			updateUniqueFieldValueMap(uniqueFieldValueMap, uniqueFieldNameMap, itemDataMap);

			//Check validation on map item
			ResponseMessage responseMessage = validate(esafeboxFieldNames, itemDataMap);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				return responseMessage;
			}
		}

		//Check duplicate unique key
		for (String uniqueFieldName : uniqueFieldValueMap.keySet()) {
			Set<String> setValues = uniqueFieldValueMap.get(uniqueFieldName);
			if (setValues.size() < listObjectData.size()) {
				return ResponseMessage.builder()
						.responseCode(DATA_IS_DUPLICATE_IN_LIST.getErrorCode())
						.responseMessage(DATA_IS_DUPLICATE_IN_LIST.getDescription(uniqueFieldName))
						.build();
			}
		}

		return ResponseMessage.builder()
				.responseCode(RESPONSE_CODE_SUCCESS_VALUE)
				.responseMessage(RESPONSE_MESSAGE_SUCCESS_VALUE)
				.build();

	}

	private void updateUniqueFieldValueMap(Map<String, Set<String>> uniqueFieldValueMap, Map<String, List<String>> uniqueFieldNameMap,
										   Map<String, Object> data) {
		if (CollectionUtils.isEmpty(uniqueFieldNameMap)) {
			return;
		}

		for (List<String> uniqueFieldNames : uniqueFieldNameMap.values()) {
			for (String uniqueFieldName : uniqueFieldNames) {
				if (data.get(uniqueFieldName) == null) {
					continue;
				}

				Set<String> setValues = Sets.newHashSet();
				if (uniqueFieldValueMap.get(uniqueFieldName) != null) {
					setValues = uniqueFieldValueMap.get(uniqueFieldName);
				}

				setValues.add(data.get(uniqueFieldName).toString());
				uniqueFieldValueMap.put(uniqueFieldName, setValues);
			}
		}
	}

    public ResponseMessage validateChannel(TbChannel channel) {
		//channel code
		if (StringUtils.isEmpty(channel.getCode())) {
			return ResponseMessage.builder()
					.responseCode("0001")
					.responseMessage("Channel code is empty")
					.build();
		}

		//channel kp
		if (StringUtils.isEmpty(channel.getrKp())) {
			return ResponseMessage.builder()
					.responseCode("0002")
					.responseMessage("Channel public key is empty")
					.build();
		}

		//channel ks
		if (StringUtils.isEmpty(channel.getrKs())) {
			return ResponseMessage.builder()
					.responseCode("0003")
					.responseMessage("Channel secret key is empty")
					.build();
		}

		//client kp
		if (StringUtils.isEmpty(channel.getrClientKp())) {
			return ResponseMessage.builder()
					.responseCode("0004")
					.responseMessage("Client public key is empty")
					.build();
		}

		//client ks
		if (StringUtils.isEmpty(channel.getrClientKs())) {
			return ResponseMessage.builder()
					.responseCode("0005")
					.responseMessage("Client secret key is empty")
					.build();
		}

		return ResponseMessage.builder()
				.responseCode(RESPONSE_CODE_SUCCESS_VALUE)
				.responseMessage(RESPONSE_MESSAGE_SUCCESS_VALUE)
				.build();
    }
    
    
	public ResponseMessage validateListObjects(String fieldName, List<EsafeboxFieldName> gatewayFieldNames,
			List<Object> listObjectData, Map<String, List<String>> uniqueFieldNameMap,
			Map<String, Set<String>> uniqueFieldValueMap) {
		if (CollectionUtils.isEmpty(listObjectData)) {
			return ResponseMessage.builder().responseCode(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode())
					.responseMessage(EsafeboxErrorCode.PARAM_IS_MISSED.getDescription(fieldName)).build();
		}

		for (Object obj : listObjectData) {
			if (obj == null) {
				log.warn("Item data is null. Please consider the input data with LIST.");
				continue;
			}

			//Convert item object to map data
			Map<String, Object> itemDataMap = EsafeboxUtils.convertObject2Map(obj);

			//update list unique fields value
			if (!CollectionUtils.isEmpty(uniqueFieldNameMap)) {
				updateUniqueFieldValueMap(uniqueFieldValueMap, uniqueFieldNameMap, itemDataMap);
			}

			//Check validation on map item
			ResponseMessage responseMessage = validate(gatewayFieldNames, itemDataMap);
			if (!EsafeboxUtils.isSuccessResponse(responseMessage)) {
				return responseMessage;
			}
		}

		//Check duplicate unique key
		for (String uniqueKeys : uniqueFieldValueMap.keySet()) {
			Set<String> setValues = uniqueFieldValueMap.get(uniqueKeys);
			String fieldNames = uniqueFieldNameMap.get(uniqueKeys).stream().collect(Collectors.joining("-"));
			if (setValues.size() < listObjectData.size()) {
				return ResponseMessage.builder().responseCode(DATA_IS_DUPLICATE_IN_LIST.getErrorCode())
						.responseMessage(DATA_IS_DUPLICATE_IN_LIST.getDescription(fieldNames)).build();
			}
		}

		return ResponseMessage.builder().responseCode(Constants.RESPONSE_CODE_SUCCESS_VALUE)
				.responseMessage(Constants.RESPONSE_MESSAGE_SUCCESS_VALUE).build();

	}
}
