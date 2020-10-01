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
public enum UomTypeFieldName {
	
	UOM_TYPE_ID(EsafeboxFieldName.builder()
			.fieldName("uomTypeId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	UOM_TYPE_CODE(EsafeboxFieldName.builder()
			.fieldName("uomTypeCode")
			.entityFieldName("code")
			.dataType(EsafeboxDataType.STRING)
			.length(8)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [uomTypeCode] is not valid. It contains the special characters or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),
	
	UOM_TYPE_NAME(EsafeboxFieldName.builder()
			.fieldName("uomTypeName")
			.entityFieldName("name")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.pattern(PatternDetail.builder()
					.pattern("^[ a-z0-9A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀẾỂưăạảấầẩẫậắằẳẵặẹẻẽềếểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵýỷỹ]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [uomTypeName] is not valid. It contains the special character(s) or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),
	UOM_TYPE_PARENT(EsafeboxFieldName.builder()
			.fieldName("parentId")
			.entityFieldName("parentId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(false)
			.build())
	;
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				UOM_TYPE_ID.getEsafeboxFieldName(),
				UOM_TYPE_CODE.getEsafeboxFieldName(),
				UOM_TYPE_NAME.getEsafeboxFieldName(),
				UOM_TYPE_PARENT.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getListUomSortFieldNames() {
		return Arrays.asList(
				UOM_TYPE_ID.getEsafeboxFieldName(),
				UOM_TYPE_CODE.getEsafeboxFieldName(),
				UOM_TYPE_NAME.getEsafeboxFieldName(),
				UOM_TYPE_PARENT.getEsafeboxFieldName()
		);
	}
}
