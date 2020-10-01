package com.ecpay.esafebox.controller.model.boxtype;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class BoxTypeCreateRequest {
	@ApiModelProperty(dataType = "String", example = "string", notes = "Boxtype's code", required = true)
	private String boxTypeCode;
	
	@ApiModelProperty(dataType = "String", example = "string", notes = "Boxtype's name")
	private String boxTypeName;
	
	@ApiModelProperty(dataType = "Long", example = "25000000", notes = "Boxtype's price", required = true)
	private String boxTypePrice;
	
	@ApiModelProperty(dataType = "Long", example = "2500000", notes = "Boxtype's price sale", required = true)
	private String boxTypeSale;
	
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Set Id", required = true)
	private String setId;
	
	@ApiModelProperty(dataType = "String", example = "Base64String", notes = "Boxtype's icon (Base64String)")
	private String boxTypeIcon;

	@Override
	public String toString() {
		return "BoxTypeCreateRequest {\"boxTypeCode\": \"" + boxTypeCode + "\", \"boxTypeName\": \"" + boxTypeName
				+ "\", \"boxTypePrice\": \"" + boxTypePrice + "\", \"boxTypeSale\": \"" + boxTypeSale
				+ "\", \"setId\": \"" + setId + "\"}";
	}
	
}


