package com.ecpay.esafebox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EsafeboxFieldName {

	String fieldName;
	String entityFieldName;
	EsafeboxDataType dataType;
	boolean isMandatory;
	int length;
	Long minValue;
	Long maxValue;
	PatternDetail pattern;
	List<String> fixedValues;
	Object defaultValue;
}
