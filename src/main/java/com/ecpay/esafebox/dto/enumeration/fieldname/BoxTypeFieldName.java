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
public enum BoxTypeFieldName {
	
	BOX_TYPE_ID(EsafeboxFieldName.builder()
			.fieldName("boxTypeId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_CODE(EsafeboxFieldName.builder()
			.fieldName("boxTypeCode")
			.entityFieldName("code")
			.dataType(EsafeboxDataType.STRING)
			.length(8)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9 ]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [boxTypeCode] is not valid. It contains the special characters or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),
	
	BOX_TYPE_NAME(EsafeboxFieldName.builder()
			.fieldName("boxTypeName")
			.entityFieldName("name")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.pattern(PatternDetail.builder()
					.pattern("^[^`~!@#$%^&*()_+={}\\[\\]|\\\\:;“’<,>.?๐฿]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [boxTypeName] is not valid. It contains the special character(s).")
					.build())
			.isMandatory(true)
			.build()),
	BOX_TYPE_SET(EsafeboxFieldName.builder()
			.fieldName("setId")
			.entityFieldName("setId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_SET_UPDATE(EsafeboxFieldName.builder()
			.fieldName("setId")
			.entityFieldName("setId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_SALE(EsafeboxFieldName.builder()
			.fieldName("boxTypeSale")
			.entityFieldName("sale")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_SALE_UPDATE(EsafeboxFieldName.builder()
			.fieldName("boxTypeSale")
			.entityFieldName("sale")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_PRICE(EsafeboxFieldName.builder()
			.fieldName("boxTypePrice")
			.entityFieldName("price")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_PRICE_UPDATE(EsafeboxFieldName.builder()
			.fieldName("boxTypePrice")
			.entityFieldName("price")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_TYPE_ICON(EsafeboxFieldName.builder()
			.fieldName("boxTypeIcon")
			.entityFieldName("icon")
			.dataType(EsafeboxDataType.STRING)
			.length(2097152)
			.isMandatory(false)
			.build())
	;
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				BOX_TYPE_CODE.getEsafeboxFieldName(),
				BOX_TYPE_NAME.getEsafeboxFieldName(),
				BOX_TYPE_PRICE.getEsafeboxFieldName(),
				BOX_TYPE_SALE.getEsafeboxFieldName(),
				BOX_TYPE_SET.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getListBoxSortFieldNames() {
		return Arrays.asList(
				BOX_TYPE_ID.getEsafeboxFieldName(),
				BOX_TYPE_CODE.getEsafeboxFieldName(),
				BOX_TYPE_NAME.getEsafeboxFieldName(),
				BOX_TYPE_PRICE.getEsafeboxFieldName(),
				BOX_TYPE_SALE.getEsafeboxFieldName(),
				BOX_TYPE_SET.getEsafeboxFieldName()
		);
	}
}
