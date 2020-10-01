package com.ecpay.esafebox.utils;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.fieldname.PaginationSortingFieldName;
import com.google.common.collect.Lists;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class PaginationUtils {

	/*
	public Pageable buildPageable(Map<String, Object> data, List<String> sorts) {

		Pageable pageableWithoutSort = buildPageableWithoutSort(data);

		//direction
		String directionValue = NotificationUtils.getFieldValueAsString(PaginationSortingFieldName.ORDER.getNotificationFieldName().getFieldName(), data);
		directionValue = directionValue.equals(Constants.PLUS)?Constants.ASC:Constants.DESC;
		String defaultDirectionValue = PaginationSortingFieldName.ORDER.getNotificationFieldName().getDefaultValue().toString();
		directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
		log.info("Direction: {}", directionValue);

		Sort.Direction direction = Sort.Direction.valueOf(directionValue.toUpperCase());

		return PageRequest.of(pageableWithoutSort.getPageNumber(), pageableWithoutSort.getPageSize(), Sort.by(direction, sorts.stream().toArray(String[]::new)));
	}*/
	
	public Pageable buildPageable(Map<String, Object> data, List<String> sorts) {

		Pageable pageableWithoutSort = buildPageableWithoutSort(data);

		//direction
		String directionValue = EsafeboxUtils.getFieldValueAsString(PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getFieldName(), data);
		String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue().toString();
		directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
		log.info("Direction: {}", directionValue);

		Sort.Direction direction = Sort.Direction.valueOf(directionValue.toUpperCase());

		return PageRequest.of(pageableWithoutSort.getPageNumber(), pageableWithoutSort.getPageSize(), Sort.by(direction, sorts.stream().toArray(String[]::new)));
	}

	public Sort buildSort(Map<String, Object> data, List<String> sorts) {

		//direction
		String directionValue = EsafeboxUtils.getFieldValueAsString(PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getFieldName(), data);
		String defaultDirectionValue = PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName().getDefaultValue().toString();
		directionValue = StringUtils.isEmpty(directionValue) ? defaultDirectionValue : directionValue;
		log.info("Direction: {}", directionValue);

		Sort.Direction direction = Sort.Direction.valueOf(directionValue.toUpperCase());

		return Sort.by(direction, sorts.stream().toArray(String[]::new));
	}

	public Pageable buildPageableWithoutSort(Map<String, Object> data) {
		//page number
		Integer pageNumber = EsafeboxUtils.getFieldValueAsInteger(PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName().getFieldName(), data);
		int defaultPageNumber = Integer.parseInt(PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName().getDefaultValue().toString());
		pageNumber = (pageNumber == null || pageNumber <= 0) ? defaultPageNumber : pageNumber;
		log.info("Page number value: {}", pageNumber);

		//page size
		Integer pageSize = EsafeboxUtils.getFieldValueAsInteger(PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName().getFieldName(), data);
		int defaultPageSize = Integer.parseInt(PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName().getDefaultValue().toString());
		pageSize = (pageSize == null || pageSize <= 0) ? defaultPageSize : pageSize;
		log.info("Page size value: {}", pageSize);

		return PageRequest.of(pageNumber - 1, pageSize);
	}
	
	public List<String> getEntitySortFieldNames(List<String> sortFieldNames, List<EsafeboxFieldName> configSortFieldNames) {
		if (CollectionUtils.isEmpty(sortFieldNames)) {
			return null;
		}

		List<String> sortEntityFieldNames = Lists.newArrayList();
		for (String fieldName : sortFieldNames) {
			EsafeboxFieldName gatewayFieldName = getGatewayFieldName(fieldName, configSortFieldNames);
			if (gatewayFieldName != null) {
				sortEntityFieldNames.add(gatewayFieldName.getEntityFieldName());
			}
		}

		return sortEntityFieldNames;
	}
	
	private EsafeboxFieldName getGatewayFieldName(String fieldName, List<EsafeboxFieldName> configSortFieldNames) {
		for (EsafeboxFieldName gatewayFieldName : configSortFieldNames) {
			if (gatewayFieldName.getFieldName().equals(fieldName)) {
				return gatewayFieldName;
			}
		}

		return null;
	}
	
	public Pageable buildPageable(Map<String, Object> data, List<String> sorts, String directionValue) {
		Pageable pageableWithoutSort = buildPageableWithoutSort(data);
		Sort.Direction direction = Sort.Direction.valueOf(directionValue.toUpperCase());
		return PageRequest.of(pageableWithoutSort.getPageNumber(), pageableWithoutSort.getPageSize(), Sort.by(direction, sorts.stream().toArray(String[]::new)));
	}
}
