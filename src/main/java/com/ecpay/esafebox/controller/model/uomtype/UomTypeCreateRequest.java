package com.ecpay.esafebox.controller.model.uomtype;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UomTypeCreateRequest {
	@ApiModelProperty(dataType = "String", example = "string", notes = "Uomtype's code", required = true)
	private String uomTypeCode;

	@ApiModelProperty(dataType = "String", example = "string", notes = "Uomtype's name")
	private String uomTypeName;

	@Override
	public String toString() {
		return "UomTypeCreateRequest {\"uomTypeCode\": \"" + uomTypeCode + "\", \"uomTypeName\": \"" + uomTypeName + "\"}";
	}

}
