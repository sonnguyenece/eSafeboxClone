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
public enum UomFieldName {
	
	UOM_ID(EsafeboxFieldName.builder()
			.fieldName("uomId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	UOM_TYPE_ID(EsafeboxFieldName.builder()
			.fieldName("uomTypeId")
			.entityFieldName("uomTypeId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	UOM_ABBREVIATION(EsafeboxFieldName.builder()
			.fieldName("uomAbbreviation")
			.entityFieldName("abbreviation")
			.dataType(EsafeboxDataType.STRING)
			.length(8)
			.pattern(PatternDetail.builder()
					.pattern("^[a-z0-9A-Z]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [code] is not valid. It contains the special character(s).")
					.build())
			.isMandatory(true)
			.build()),
	
	UOM_NAME(EsafeboxFieldName.builder()
			.fieldName("uomName")
			.entityFieldName("name")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.pattern(PatternDetail.builder()
					.pattern("^[ a-z0-9A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀẾỂưăạảấầẩẫậắằẳẵặẹẻẽềếểỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừỬỮỰỲỴÝỶỸửữựỳỵýỷỹ]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [code] is not valid. It contains the special character(s).")
					.build())
			.isMandatory(true)
			.build())
	;
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				UOM_ID.getEsafeboxFieldName(),
				UOM_TYPE_ID.getEsafeboxFieldName(),
				UOM_ABBREVIATION.getEsafeboxFieldName(),
				UOM_NAME.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getListUomSortFieldNames() {
		return Arrays.asList(
				UOM_ID.getEsafeboxFieldName(),
				UOM_TYPE_ID.getEsafeboxFieldName(),
				UOM_ABBREVIATION.getEsafeboxFieldName(),
				UOM_NAME.getEsafeboxFieldName()
		);
	}
}
