package com.ecpay.esafebox.dto.enumeration.fieldname;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PaginationSortingFieldName {

	/**
	 * PAGINATION & SORTING
	 */
	PAGE_NUMBER(EsafeboxFieldName.builder()
			.fieldName("pageNumber")
			.dataType(EsafeboxDataType.INTEGER)
			.isMandatory(false)
			.defaultValue(1)
			.build()),
    PAGE_SIZE(EsafeboxFieldName.builder()
			.fieldName("pageSize")
			.dataType(EsafeboxDataType.INTEGER)
			.isMandatory(false)
			.defaultValue(50)
			.build()),
    //ORDER
    SORTS(EsafeboxFieldName.builder()
			.fieldName("sorts")
			.dataType(EsafeboxDataType.ARRAY)
			.build()),
    //SORT
    DIRECTION(EsafeboxFieldName.builder()
			.fieldName("order")
			.dataType(EsafeboxDataType.STRING)
			.isMandatory(false)
			.defaultValue("asc")
			.fixedValues(Arrays.asList("asc", "desc"))
			.build());

	EsafeboxFieldName esafeboxFieldName;

	public static List<EsafeboxFieldName> getListEsafeboxFieldNames() {
		return Arrays.stream(values())
				.map(PaginationSortingFieldName::getEsafeboxFieldName)
				.collect(Collectors.toList());
	}

}
