package com.ecpay.esafebox.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

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
public class LimitDto {
	Long limitId;
	String limitCode;
	String limitName;
	Long limitType;
	Long boxTypeId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<LimitValueDto> listLimitValue;
}
