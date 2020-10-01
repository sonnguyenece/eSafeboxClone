package com.ecpay.esafebox.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel
public class BoxtypeValueDto {
	private Long valueId;
	private Long boxTypeId;
	private Long attributeId;
	private String attributeValue;
}
