package com.ecpay.esafebox.controller.model.uomtype;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UomTypeUpdateRequest extends UomTypeCreateRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Uomtype's id", required = true)
	private String uomTypeId;
}
