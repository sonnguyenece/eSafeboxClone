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
public enum LimitFieldName {
	
	LIMIT_ID(EsafeboxFieldName.builder()
			.fieldName("limitId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	LIMIT_CODE(EsafeboxFieldName.builder()
			.fieldName("limitCode")
			.entityFieldName("code")
			.dataType(EsafeboxDataType.STRING)
			.length(8)
			.pattern(PatternDetail.builder()
					.pattern("^[a-z0-9A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀẾỂưăạảấầẩẫậắằẳẵặẹẻẽềếểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵýỷỹ]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [code] is not valid. It contains the special character(s).")
					.build())
			.isMandatory(true)
			.build()),
	
	LIMIT_NAME(EsafeboxFieldName.builder()
			.fieldName("limitName")
			.entityFieldName("name")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.pattern(PatternDetail.builder()
					.pattern("^[a-z0-9A-Z _ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀẾỂưăạảấầẩẫậắằẳẵặẹẻẽềếểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵýỷỹ]*$")
					.description("Pattern for name")
					.errorMessage("Data for field [name] is not valid. It contains the special character(s).")
					.build())
			.isMandatory(true)
			.build()),
	LIMIT_TYPE(EsafeboxFieldName.builder()
			.fieldName("limitType")
			.entityFieldName("type")
			.dataType(EsafeboxDataType.LONG)
			.pattern(PatternDetail.builder()
					.pattern("[1|2|3]")
					.description("Pattern for limitType")
					.errorMessage("Data for field [code] is not valid. It accepts only 1 or 2 or 3.")
					.build())
			.isMandatory(true)
			.build()),
	LIMIT_BOX_TYPE_ID(EsafeboxFieldName.builder()
			.fieldName("boxTypeId")
			.entityFieldName("boxTypeId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build())
	;
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				LIMIT_ID.getEsafeboxFieldName(),
				LIMIT_CODE.getEsafeboxFieldName(),
				LIMIT_NAME.getEsafeboxFieldName(),
				LIMIT_TYPE.getEsafeboxFieldName(),
				LIMIT_BOX_TYPE_ID.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getListBoxTypeLimitSortFieldNames() {
		return Arrays.asList(
				LIMIT_ID.getEsafeboxFieldName(),
				LIMIT_CODE.getEsafeboxFieldName(),
				LIMIT_NAME.getEsafeboxFieldName(),
				LIMIT_TYPE.getEsafeboxFieldName(),
				LIMIT_BOX_TYPE_ID.getEsafeboxFieldName()
		);
	}
}
