package com.ecpay.esafebox.controller.model.value;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class CreateBoxTypeValueRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Boxtype id", required = true)
	private String boxTypeId;

	@ApiModelProperty(dataType = "Object", required = true, example = "[{\"attributeId\":null,\"attributeValue\":\"\"}]", notes = "boxTypeAttributes")
	private Object boxTypeAttributes;


	@Override
	public String toString() {
		return "CreateBoxTypeValueRequest {\"boxTypeId\": \"" + boxTypeId + "\", \"boxTypeAttributes\": \""
				+ boxTypeAttributes + "\"}";
	}

}
