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
public enum TransactionFieldName {
	TRANSACTION_ID(EsafeboxFieldName.builder()
			.fieldName("transactionId")
			.entityFieldName("id")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	SENDER(EsafeboxFieldName.builder()
			.fieldName("sender")
			.entityFieldName("sender")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.defaultValue(0)
			.isMandatory(true)
			.build()),
	RECEIVER(EsafeboxFieldName.builder()
			.fieldName("receiver")
			.entityFieldName("receiver")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.defaultValue(0)
			.isMandatory(true)
			.build()),
	FROM_DATE(EsafeboxFieldName.builder()
			.fieldName("fromDate")
			.entityFieldName("transactionTime")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(false)
			.defaultValue(LocalDateTime.now())
			.pattern(PatternDetail.builder()
					.pattern("yyyyMMdd")
					.description("Pattern for date")
					.errorMessage("Data for field [fromDate] is not valid. It must be in format yyyyMMdd")
					.build())
			.build()),
	TO_DATE(EsafeboxFieldName.builder()
			.fieldName("toDate")
			.entityFieldName("transactionTime")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(false)
			.defaultValue(LocalDateTime.now())
			.pattern(PatternDetail.builder()
					.pattern("yyyyMMdd")
					.description("Pattern for date")
					.errorMessage("Data for field [toDate] is not valid. It must be in format yyyyMMdd")
					.build())
			.build()),
	TRANSACTION_TIME(EsafeboxFieldName.builder()
			.fieldName("transactionTime")
			.entityFieldName("transactionTime")
			.dataType(EsafeboxDataType.TIMESTAMP)
			.isMandatory(false)
			.defaultValue(LocalDateTime.now())
			.pattern(PatternDetail.builder()
					.pattern("yyyyMMdd")
					.description("Pattern for date")
					.errorMessage("Data for field [transactionTime] is not valid. It must be in format yyyyMMdd")
					.build())
			.build()),
	TRANSACTION_TYPE(EsafeboxFieldName.builder()
				.fieldName("transactionType")
				.entityFieldName("transactionDataType")
				.dataType(EsafeboxDataType.STRING)
				.isMandatory(false)
				.defaultValue("0")
				.fixedValues(Arrays.asList("DS", "CS", "SS", "SD", "0"))
				.build());
	EsafeboxFieldName esafeboxFieldName;
	
	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				SENDER.getEsafeboxFieldName(),
				RECEIVER.getEsafeboxFieldName(),
				FROM_DATE.getEsafeboxFieldName(),
				TO_DATE.getEsafeboxFieldName(),
				TRANSACTION_TYPE.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}
	
	public static List<EsafeboxFieldName> getSortFieldNames() {
		return Arrays.asList(
				TRANSACTION_ID.getEsafeboxFieldName(),
				SENDER.getEsafeboxFieldName(),
				RECEIVER.getEsafeboxFieldName(),
				FROM_DATE.getEsafeboxFieldName(),
				TO_DATE.getEsafeboxFieldName(),
				TRANSACTION_TYPE.getEsafeboxFieldName()
		);
	}
}
