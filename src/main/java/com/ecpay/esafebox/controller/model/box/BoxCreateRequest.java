package com.ecpay.esafebox.controller.model.box;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class BoxCreateRequest {
	@ApiModelProperty(dataType = "String", example = "MEUCIQCGQ", notes = "manufacture code")
	private String manufactureCode;
	
	@ApiModelProperty(dataType = "Long", example = "4545", notes = "boxType's id", required = true)
	private String boxTypeId;

	@ApiModelProperty(dataType = "Long", example = "50", notes = "quantity")
	private String quantityBox;

	@Override
	public String toString() {
		return "BoxCreateRequest {\"manufactureCode\": \"" + manufactureCode + "\", \"boxTypeId\": \"" + boxTypeId
				+ "\", \"quantityBox\": \"" + quantityBox + "\"}";
	}

}
