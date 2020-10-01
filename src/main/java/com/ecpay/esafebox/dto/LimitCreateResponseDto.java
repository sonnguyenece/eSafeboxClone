package com.ecpay.esafebox.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitCreateResponseDto {
	Long limitId;
	String limitCode;
	String limitName;
	Long limitType;
	Long boxTypeId;
	Long limitValueId;
	Long limitValue;
	Long attributeId;
}
