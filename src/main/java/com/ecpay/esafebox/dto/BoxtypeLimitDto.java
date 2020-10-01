package com.ecpay.esafebox.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class BoxtypeLimitDto {
	Long limitId;
	String limitCode;
	String limitName;
	String limitType;
	Long boxTypeId;
	Long boxTypeValueId;
	String limitValue;
}
