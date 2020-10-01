package com.ecpay.esafebox.dto.enumeration.fieldname;

import java.util.Arrays;
import java.util.List;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ValueFieldName {

	BOX_TYPE_ID(EsafeboxFieldName.builder().fieldName("boxTypeId").entityFieldName("boxTypeId")
			.dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999999L).isMandatory(true).build()),
	BOX_TYPE_ATTRIBUTES(EsafeboxFieldName.builder().fieldName("boxTypeAttributes")
			.dataType(EsafeboxDataType.JSON).isMandatory(true).build());

	EsafeboxFieldName esafeboxFieldName;

	public static List<EsafeboxFieldName> getSearchFieldNames() {
		return Arrays.asList(BOX_TYPE_ID.getEsafeboxFieldName(),
				BOX_TYPE_ATTRIBUTES.getEsafeboxFieldName());
	}
}
