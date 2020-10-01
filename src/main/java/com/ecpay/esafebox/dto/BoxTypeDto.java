package com.ecpay.esafebox.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoxTypeDto {
	Long boxTypeId;
	String boxTypeCode;
	String boxTypeName;
	Long boxTypePrice;
	Long boxTypeSale;
	Long setId;
	String setName;
	String boxTypeIcon;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<BoxtypeValueDto> boxTypeAttributes;
}
