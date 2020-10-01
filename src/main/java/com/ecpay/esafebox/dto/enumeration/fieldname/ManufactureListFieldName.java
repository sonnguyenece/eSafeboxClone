package com.ecpay.esafebox.dto.enumeration.fieldname;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

import com.ecpay.esafebox.dto.PatternDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ManufactureListFieldName {

	/**
	 * MANUFACTURELIST
	 */
	MANUFACTURE_CODE(EsafeboxFieldName.builder()
			.fieldName("manufactureCode")
			.entityFieldName("code")
			.dataType(EsafeboxDataType.STRING)
			.length(16)
			.pattern(PatternDetail.builder()
					.pattern("^[A-Za-z0-9]*$")
					.description("Pattern for code")
					.errorMessage("Data for field [code] is not valid. It contains the special characters or accented Vietnamese")
					.build())
			.isMandatory(true)
			.build()),
	MANUFACTURE_BOXTYPE(EsafeboxFieldName.builder()
			.fieldName("boxTypeId")
			.entityFieldName("boxtypeId")
			.dataType(EsafeboxDataType.LONG)
			.minValue(0L)
			.maxValue(999999999999999999L)
			.isMandatory(true)
			.build()),
	FACTURE_STATUS(EsafeboxFieldName.builder()
			.fieldName("factureStatus")
			.entityFieldName("status")
			.dataType(EsafeboxDataType.STRING)
			.length(1)
			.pattern(PatternDetail.builder()
					.pattern("^(?:C\\b|P\\b|S)")
					.description("Pattern for status")
					.errorMessage("Data for field [status] is not valid. [status] must be C or P or S")
					.build())
			.isMandatory(true)
			.build()),
	BOX_STATUS(EsafeboxFieldName.builder()
			.fieldName("boxStatus")
			.dataType(EsafeboxDataType.STRING)
			.length(1)
			.pattern(PatternDetail.builder()
					.pattern("^(?:N\\b|Y\\b|P)")
					.description("Pattern for status")
					.errorMessage("Data for field [status] is not valid. [status] must be N or Y or P")
					.build())
			.isMandatory(true)
			.build()),
	CREATED_DATE(EsafeboxFieldName.builder()
			.fieldName("createdDate")
			.entityFieldName("created")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.build()),
	FROM_DATE(EsafeboxFieldName.builder()
			.fieldName("fromDate")
			.entityFieldName("created")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.build()),
	TO_DATE(EsafeboxFieldName.builder()
			.fieldName("toDate")
			.entityFieldName("created")
			.dataType(EsafeboxDataType.DATE)
			.isMandatory(true)
			.defaultValue(LocalDateTime.now())
			.build()),
	//FOR SORT
	SORTS(EsafeboxFieldName.builder()
			.fieldName("orderBy")
			.dataType(EsafeboxDataType.ARRAY)
			.isMandatory(false)
			.fixedValues(
				Arrays.asList(
						MANUFACTURE_CODE.getEsafeboxFieldName().getEntityFieldName(),
						MANUFACTURE_BOXTYPE.getEsafeboxFieldName().getEntityFieldName(),
						FACTURE_STATUS.getEsafeboxFieldName().getEntityFieldName(),
						BOX_STATUS.getEsafeboxFieldName().getEntityFieldName(),
						FROM_DATE.getEsafeboxFieldName().getEntityFieldName(),
						TO_DATE.getEsafeboxFieldName().getEntityFieldName()
				)
			).build());

	EsafeboxFieldName esafeboxFieldName;

	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(
				MANUFACTURE_CODE.getEsafeboxFieldName(),
				MANUFACTURE_BOXTYPE.getEsafeboxFieldName(),
				FACTURE_STATUS.getEsafeboxFieldName(),
				BOX_STATUS.getEsafeboxFieldName(),
				FROM_DATE.getEsafeboxFieldName(),
				TO_DATE.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
				PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
				PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
				PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
		);
	}

	public static List<EsafeboxFieldName> getSortFieldNames() {
		return Arrays.asList(
				MANUFACTURE_CODE.getEsafeboxFieldName(),
				MANUFACTURE_BOXTYPE.getEsafeboxFieldName(),
				FACTURE_STATUS.getEsafeboxFieldName(),
				CREATED_DATE.getEsafeboxFieldName()
		);
	}
}
