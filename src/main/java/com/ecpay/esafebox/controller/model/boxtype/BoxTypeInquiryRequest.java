package com.ecpay.esafebox.controller.model.boxtype;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class BoxTypeInquiryRequest {
	@ApiModelProperty(dataType = "String", example = "AAA", notes = "Boxtype's Code", required = true)
	private String boxTypeCode;
}
