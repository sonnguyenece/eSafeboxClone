package com.ecpay.esafebox.dto.enumeration.fieldname;

import java.util.Arrays;
import java.util.List;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.PatternDetail;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserPairFieldName {

	USER_PAIR_ID(EsafeboxFieldName.builder().fieldName("userPairId").entityFieldName("id")
			.dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999999L).isMandatory(true).build()),
	USER_PAIR_USER_FROM(EsafeboxFieldName.builder().fieldName("userPairUserFrom").entityFieldName("userPairUserFrom")
			.dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999999L).isMandatory(true).build()),
	USER_PAIR_USER_TO(EsafeboxFieldName.builder().fieldName("userPairUserTo").entityFieldName("userPairUserTo")
			.dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999999L).isMandatory(true).build()),
	USER_PAIR_USER_TO_LIST(EsafeboxFieldName.builder().fieldName("userToList")
			.dataType(EsafeboxDataType.ARRAY_OF_LONG).isMandatory(true).build()),
	USER_PAIR_STATUS(EsafeboxFieldName.builder().fieldName("userPairStatus").entityFieldName("userPairStatus")
			.dataType(EsafeboxDataType.STRING).length(1).pattern(PatternDetail.builder().pattern("[Y|N|0]")
					.description("Pattern for code").errorMessage("Data for field [code] is not valid.").build())
			.isMandatory(true).build());

	EsafeboxFieldName esafeboxFieldName;

	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(USER_PAIR_ID.getEsafeboxFieldName(), USER_PAIR_USER_FROM.getEsafeboxFieldName(),
				USER_PAIR_USER_TO.getEsafeboxFieldName(), USER_PAIR_STATUS.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName());
	}

	public static List<EsafeboxFieldName> getListUomSortFieldNames() {
		return Arrays.asList(USER_PAIR_ID.getEsafeboxFieldName(), USER_PAIR_USER_FROM.getEsafeboxFieldName(),
				USER_PAIR_USER_TO.getEsafeboxFieldName(), USER_PAIR_STATUS.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName());
	}
}
