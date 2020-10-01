package com.ecpay.esafebox.dto.enumeration.fieldname;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.PatternDetail;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Arrays;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EdongEsafeboxFieldName {
	KEYBOX_CLIENT(EsafeboxFieldName.builder()
			.fieldName("keyBoxClient")
			.dataType(EsafeboxDataType.STRING)
			.length(4000)
			.isMandatory(true)
			.build()),
	
	TERMINAL_ID(EsafeboxFieldName.builder()
			.fieldName("terminalId")
			.entityFieldName("terminalId")
			.dataType(EsafeboxDataType.STRING)
			.length(256)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9]*$")
					.description("Pattern for terminalId")
					.errorMessage("Data for field [terminalId] is not valid. It contains the special characters or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),
	
	TERMINAL_INFO(EsafeboxFieldName.builder()
			.fieldName("terminalInfo")
			.entityFieldName("terminalInfo")
			.dataType(EsafeboxDataType.STRING)
			.length(256)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9 ]*$")
					.description("Pattern for terminalInfo")
					.errorMessage("Data for field [terminalInfo] is not valid. It contains the special characters or accented Vietnamese.")
					.build())
			.isMandatory(true)
			.build()),

	RECEIVER_SERIAL(EsafeboxFieldName.builder()
			.fieldName("receiver")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(99999999999L)
			.isMandatory(true)
			.build()),
	
	SENDER(EsafeboxFieldName.builder()
			.fieldName("sender")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.isMandatory(true)
			.build()),
	
	TYPE(EsafeboxFieldName.builder()
			.fieldName("sender")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.isMandatory(true)
			.fixedValues(Arrays.asList("SS", "SD", "DS", "ES"))
			.build()),
	
	CONTENTS(EsafeboxFieldName.builder()
			.fieldName("content")
			.dataType(EsafeboxDataType.STRING)
			.length(4000)
			.isMandatory(true)
			.build()),
	
	LIST_ITEMS(EsafeboxFieldName.builder()
			.fieldName("items")
			.dataType(EsafeboxDataType.ARRAY)
			.isMandatory(true)
			.build()),
	
	ISSUER_CODE(EsafeboxFieldName.builder()
			.fieldName("issuerCode")
			.dataType(EsafeboxDataType.STRING)
			.length(128)
			.isMandatory(true)
			.build()),
	
    ITEM_VALUES(EsafeboxFieldName.builder()
			.fieldName("values")
			.dataType(EsafeboxDataType.ARRAY_OF_STRING)
			.isMandatory(true)
			.build()),
    
	ITEM_QUANTITIES(EsafeboxFieldName.builder()
			.fieldName("quantities")
			.dataType(EsafeboxDataType.ARRAY_OF_LONG)
			.isMandatory(true)
			.build()),
	;
	EsafeboxFieldName esafeboxFieldName;
	
	
}
