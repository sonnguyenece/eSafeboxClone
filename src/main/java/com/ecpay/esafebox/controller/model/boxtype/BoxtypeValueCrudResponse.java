package com.ecpay.esafebox.controller.model.boxtype;

import io.swagger.annotations.ApiModelProperty;

public class BoxtypeValueCrudResponse {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Boxtype's value Id", required = true)
	String valueId;
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Boxtype's attribute Id", required = true)
	String attributeId;
	@ApiModelProperty(dataType = "Long", example = "0", notes = "Boxtype's attribute value", required = true)
	String attributeValue;
	@Override
	public String toString() {
		return "{\"valueId\": \"" + valueId + "\", \"attributeId\": \"" + attributeId
				+ "\", \"attributeValue\": \"" + attributeValue + "\"}";
	}
}
