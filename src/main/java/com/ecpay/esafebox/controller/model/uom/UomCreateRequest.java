package com.ecpay.esafebox.controller.model.uom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UomCreateRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "UOM type id")
	private String uomTypeId;
	
	@ApiModelProperty(dataType = "String", example = "string", notes = "Uom's abbreviation", required = true)
	private String uomAbbreviation;

	@ApiModelProperty(dataType = "String", example = "string", notes = "Uom's name")
	private String uomName;

	@Override
	public String toString() {
		return "UomCreateRequest {\"uomTypeId\": \"" + uomTypeId + "\", \"uomAbbreviation\": \"" + uomAbbreviation
				+ "\", \"uomName\": \"" + uomName + "\"}";
	}

}
