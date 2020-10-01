package com.ecpay.esafebox.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecpay.entities.ecbox.Attribute;
import com.ecpay.entities.ecbox.Value;
import com.ecpay.esafebox.dto.ValueDto;
import com.ecpay.esafebox.dto.ValueDtos;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.dto.enumeration.fieldname.ValueFieldName;
import com.ecpay.esafebox.exception.ESafeboxException;
import com.ecpay.esafebox.mapper.ValueMapper;
import com.ecpay.esafebox.repository.AttributeDslRepository;
import com.ecpay.esafebox.repository.ValueDslRepository;
import com.ecpay.esafebox.utils.Constants;
import com.ecpay.esafebox.utils.EsafeboxUtils;

@Service
@Transactional
public class ValueDslService {

	private static final Logger logger = LogManager.getLogger(Constants.LOGGER_APPENDER.SERVICE);
	@Autowired
	private ValueDslRepository valueDslRepository;
	@Autowired
	private AttributeDslRepository attributeDslRepository;

	@Autowired
	private ValueMapper valueMapper;

	public Value getUniqueBoxtype(Long id) {
		return valueDslRepository.findOneById(id);
	}

	@SuppressWarnings("unchecked")
	public ValueDtos createBoxTypeValue(Long logId, Map<String, Object> data) throws ESafeboxException {
		logger.info("[{}] Saving boxtype {}", logId, data);
		Long boxTypeId = EsafeboxUtils
				.getFieldValueAsLong(ValueFieldName.BOX_TYPE_ID.getEsafeboxFieldName().getFieldName(), data);

//		Iterator<JsonNode> boxTypeAttributeIterator = boxTypeAttributes.elements();
		List<?> lh = (ArrayList<?>) data.get(ValueFieldName.BOX_TYPE_ATTRIBUTES.getEsafeboxFieldName().getFieldName());
		Iterator<?> boxTypeAttributeIterator = lh.iterator();

		// check UC005 throw S033
		if (!boxTypeAttributeIterator.hasNext()) {
			logger.info("[{}] Checking existing Boxtype FAIL - Reason: Box type attribute is empty --> END.", logId);
			throw new ESafeboxException(EsafeboxErrorCode.BOX_TYPE_ATTRIBUTE_DOES_NOT_ALLOW_EMPTY.getErrorCode(),
					EsafeboxErrorCode.BOX_TYPE_ATTRIBUTE_DOES_NOT_ALLOW_EMPTY.getDescription());
		}
		List<Value> listValue = new ArrayList<Value>();
		ValueDtos valueDtos = new ValueDtos();
		String failedAttributeId = "";
		Map<String, Object> boxTypeAttributeNode = null;
		while (boxTypeAttributeIterator.hasNext()) {
			boxTypeAttributeNode = (Map<String, Object>) boxTypeAttributeIterator.next();

			// check UC001
			if (!boxTypeAttributeNode.containsKey("attributeId")) {
				logger.info("[{}] Checking existing Boxtype FAIL - Reason: Parameter [{}] is missed --> END.", logId,
						"attributeId");
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED
								.getDescription(Arrays.asList(String.valueOf(boxTypeId), "attributeId")));
			}
			if (!boxTypeAttributeNode.containsKey("attributeValue")) {
				logger.info("[{}] Checking existing Boxtype FAIL - Reason: Parameter [{}] is missed --> END.", logId,
						"attributeValue");
				throw new ESafeboxException(EsafeboxErrorCode.PARAM_IS_MISSED.getErrorCode(),
						EsafeboxErrorCode.PARAM_IS_MISSED
								.getDescription(Arrays.asList(String.valueOf(boxTypeId), "attributeValue")));
			}

			// check UC007
			if (Objects.isNull(boxTypeAttributeNode.get("attributeId")) || boxTypeAttributeNode.get("attributeId").toString().equals("")) {

				if (!Objects.isNull(boxTypeAttributeNode.get("attributeValue")) && !boxTypeAttributeNode.get("attributeValue").toString().equals("")) {
					logger.info(
							"[{}] Checking existing Boxtype FAIL - Reason: attributeId/attributeValue not allow empty. --> END.",
							logId);
					throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_NOT_ALLOW_EMPTY.getErrorCode(),
							EsafeboxErrorCode.ATTRIBUTE_NOT_ALLOW_EMPTY
									.getDescription(Arrays.asList(String.valueOf(boxTypeId))));
				} else {
					List<Value> value = valueDslRepository.findByBoxtypeId(boxTypeId);
					valueDslRepository.deleteAll(value);
					valueDtos.setBoxTypeId(boxTypeId);
					valueDtos.setBoxTypeAttributes(Collections.emptyList());
					return valueDtos;
				}
			}

			// check UC009
			Long attributeId, attributeValue;
			try {
				attributeId = Long.valueOf(boxTypeAttributeNode.get("attributeId").toString());
				attributeValue = Long.valueOf(boxTypeAttributeNode.get("attributeValue").toString());
				Optional<Attribute> oa = attributeDslRepository.findAttributeById(attributeId);
				if (!oa.isPresent()) {
					if (failedAttributeId.equals(""))
						failedAttributeId = failedAttributeId.concat(String.valueOf(attributeId));
					else
						failedAttributeId = String.join(", ", String.valueOf(attributeId));
				}
			} catch (NumberFormatException e) {
				logger.info(
						"[{}] Checking existing Boxtype FAIL - Reason: Data type of field [{}] is invalid. It is required data type is [{}]. --> END.",
						logId, "attributeId", "Long");
				throw new ESafeboxException(EsafeboxErrorCode.DATA_IS_INVALID_DATA_TYPE.getErrorCode(),
						EsafeboxErrorCode.DATA_IS_INVALID_DATA_TYPE
								.getDescription(Arrays.asList(String.valueOf(boxTypeId), "attributeId", "Long")));
			}
			Value value = Value.builder().boxTypeId(boxTypeId).attributeId(attributeId)
					.genericValue(String.valueOf(attributeValue)).build();

			listValue.add(value);
		}

		// check UC009 throw S015
		if (failedAttributeId.equals("")) {
			Map<Long, Value> hValues = new HashMap<Long, Value>();
			Iterator<Value> i = listValue.iterator();
			while (i.hasNext()) {
				Value v = i.next();
				Optional<Value> ov = valueDslRepository.findByBoxtypeIdandAttrId(boxTypeId, v.getAttributeId());
				if (ov.isPresent()) {
					// update
					valueDslRepository.updateByBoxtypeIdandAttrId(boxTypeId, ov.get().getAttributeId(), v.getGenericValue());
					v.setId(ov.get().getId());
				} else {
					valueDslRepository.saveAndFlush(v);
				}
				hValues.put(v.getId(), v);
			}

			List<ValueDto> listValueDto = new ArrayList<Value>(hValues.values()).stream().map(t -> {
				ValueDto vdto = valueMapper.toValueDto(t);
				vdto.setValueId(t.getId());
				return vdto;
//				return valueMapper.toValueDto(t);
			}).collect(Collectors.toList());
			valueDtos.setBoxTypeId(boxTypeId);
			valueDtos.setBoxTypeAttributes(listValueDto);
		} else {
			logger.info("[{}] Checking existing BoxtypeValue FAIL - Reason: attributeId [{}] does not exists. --> END.",
					logId, failedAttributeId);
			throw new ESafeboxException(EsafeboxErrorCode.ATTRIBUTE_ID_NOT_EXISTS.getErrorCode(),
					EsafeboxErrorCode.ATTRIBUTE_ID_NOT_EXISTS
							.getDescription(Arrays.asList(String.valueOf(boxTypeId), failedAttributeId)));
		}

		return valueDtos;
	}
}
