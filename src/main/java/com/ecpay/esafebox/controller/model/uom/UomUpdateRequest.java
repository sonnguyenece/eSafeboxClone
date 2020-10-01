package com.ecpay.esafebox.controller.model.uom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UomUpdateRequest extends UomCreateRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Uom's id", required = true)
	private String uomId;
}
