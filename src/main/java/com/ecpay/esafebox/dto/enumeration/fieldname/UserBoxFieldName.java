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
public enum UserBoxFieldName {
	
	/**
	 * BOX
	 */
	USERBOX_ID(EsafeboxFieldName.builder()
			.fieldName("userBoxId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	
	USER_ID(EsafeboxFieldName.builder()
			.fieldName("userId")
			.entityFieldName("userId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	
	BOX_ID(EsafeboxFieldName.builder()
			.fieldName("boxId")
			.entityFieldName("boxId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	
	BOX_SERIAL(EsafeboxFieldName.builder()
			.fieldName("boxSerial")
			.entityFieldName("box.serial")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	
	BOX_TYPE(EsafeboxFieldName.builder()
			.fieldName("boxTypeId")
			.entityFieldName("box.manufacture.boxtypeId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	
	FROM_DATE(EsafeboxFieldName.builder()
			.fieldName("fromDate")
			.entityFieldName("regiDate")
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
			.entityFieldName("regiDate")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.pattern(PatternDetail.builder()
					.pattern("yyyyMMdd")
					.description("Pattern for date")
					.errorMessage("Data for field [toDate] is not valid. It must be in format yyyyMMdd")
					.build())
			.build()),
	TERMINAL_ID(EsafeboxFieldName.builder()
			.fieldName("terminalId")
			.entityFieldName("terminalId")
			.dataType(EsafeboxDataType.STRING)
			.length(8)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [terminalId] is not valid. It contains the special characters or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),
	
	TERMINAL_INFO(EsafeboxFieldName.builder()
			.fieldName("terminalInfo")
			.entityFieldName("terminalInfo")
			.dataType(EsafeboxDataType.STRING)
			.length(8)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [terminalInfo] is not valid. It contains the special characters or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),
	
	USERBOX_ALIAS(EsafeboxFieldName.builder()
			.fieldName("alias")
			.entityFieldName("alias")
			.dataType(EsafeboxDataType.STRING)
			.length(36)
			.build()),
	
	USERBOX_KP(EsafeboxFieldName.builder()
			.fieldName("publicKey")
			.entityFieldName("boxKp")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.build()),
	
	USERBOX_ALGORITHM(EsafeboxFieldName.builder()
			.fieldName("algorithm")
			.dataType(EsafeboxDataType.STRING)
			.length(32)
			.build()),
	;
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				USER_ID.getEsafeboxFieldName(),
				BOX_TYPE.getEsafeboxFieldName(),
				BOX_SERIAL.getEsafeboxFieldName(),
				FROM_DATE.getEsafeboxFieldName(),
				TO_DATE.getEsafeboxFieldName(),
				TERMINAL_ID.getEsafeboxFieldName(),
				TERMINAL_INFO.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getListBoxSortFieldNames() {
		return Arrays.asList(
				USERBOX_ID.getEsafeboxFieldName(),
				BOX_ID.getEsafeboxFieldName(),
				BOX_TYPE.getEsafeboxFieldName(),
				BOX_SERIAL.getEsafeboxFieldName(),
				FROM_DATE.getEsafeboxFieldName(),
				TERMINAL_ID.getEsafeboxFieldName(),
				TERMINAL_INFO.getEsafeboxFieldName()
		);
	}
}
