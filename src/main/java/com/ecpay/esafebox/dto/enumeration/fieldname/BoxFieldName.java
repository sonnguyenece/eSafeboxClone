package com.ecpay.esafebox.dto.enumeration.fieldname;

import java.time.LocalDateTime;
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
public enum BoxFieldName {
	
	/**
	 * BOX
	 */
	BOX_ID(EsafeboxFieldName.builder()
			.fieldName("boxId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	BOX_SERIAL(EsafeboxFieldName.builder()
			.fieldName("boxSerial")
			.entityFieldName("serial")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	
	BOX_STATUS(EsafeboxFieldName.builder()
			.fieldName("boxStatus")
			.entityFieldName("status")
			.dataType(EsafeboxDataType.STRING)
			.length(16)
			.isMandatory(true)
			.build()),
	/**
	 * MANUFACTURE
	 */
	MANUFACTURE_CODE(EsafeboxFieldName.builder()
			.fieldName("manufactureCode")
			.entityFieldName("manufacture.code")
			.dataType(EsafeboxDataType.STRING)
			.length(16)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [manufactureCode] is not valid. It contains the special characters or accented Vietnamese")
					.build())
			.isMandatory(true)
			.build()),
	BOX_TYPE(EsafeboxFieldName.builder()
			.fieldName("boxTypeId")
			.entityFieldName("manufacture.boxtypeId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	FROM_DATE(EsafeboxFieldName.builder()
			.fieldName("fromDate")
			.entityFieldName("created")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.pattern(PatternDetail.builder()
					.pattern("yyyyMMdd")
					.description("Pattern for date")
					.errorMessage("Data for field [fromDate] is not valid. It must be in format yyyyMMdd")
					.build())
			.build()),
	TO_DATE(EsafeboxFieldName.builder()
			.fieldName("toDate")
			.entityFieldName("created")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.pattern(PatternDetail.builder()
					.pattern("yyyyMMdd")
					.description("Pattern for date")
					.errorMessage("Data for field [toDate] is not valid. It must be in format yyyyMMdd")
					.build())
			.build()),
	CREATED_DATE(EsafeboxFieldName.builder()
			.fieldName("createdDate")
			.entityFieldName("created")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.build()),
	;
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				MANUFACTURE_CODE.getEsafeboxFieldName(),
				BOX_TYPE.getEsafeboxFieldName(),
				BOX_SERIAL.getEsafeboxFieldName(),
				BOX_STATUS.getEsafeboxFieldName(),
				FROM_DATE.getEsafeboxFieldName(),
				TO_DATE.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getListBoxSortFieldNames() {
		return Arrays.asList(
				BOX_ID.getEsafeboxFieldName(),
				MANUFACTURE_CODE.getEsafeboxFieldName(),
				BOX_TYPE.getEsafeboxFieldName(),
				BOX_SERIAL.getEsafeboxFieldName(),
				BOX_STATUS.getEsafeboxFieldName(),
				CREATED_DATE.getEsafeboxFieldName()
		);
	}
}
