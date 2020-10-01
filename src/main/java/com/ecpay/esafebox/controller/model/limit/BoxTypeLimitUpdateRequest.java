package com.ecpay.esafebox.controller.model.limit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class BoxTypeLimitUpdateRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "limit id", required = true)
	private String limitId;

	@ApiModelProperty(dataType = "String", example = "HẠN MỨC GỬI", notes = "limit name")
	private String limitName;

	@ApiModelProperty(dataType = "Long", example = "1", notes = "1|2|3", required = true)
	private String limitType;

	@ApiModelProperty(dataType = "String", example = "1", notes = "box type id", required = true)
	private String boxTypeId;

	@ApiModelProperty(dataType = "Long", example = "1", notes = "limit value id", required = true)
	private String limitValueId;

	@ApiModelProperty(dataType = "String", example = "1000000", notes = "limit value", required = true)
	private String limitValue;

	@ApiModelProperty(dataType = "String", example = "1", notes = "attribute id", required = true)
	private String attributeId;

	@Override
	public String toString() {
		return "BoxTypeLimitUpdateRequest {\"limitId\": \"" + limitId + "\", \"limitName\": \"" + limitName
				+ "\", \"limitType\": \"" + limitType + "\", \"boxTypeId\": \"" + boxTypeId + "\", \"limitValueId\": \""
				+ limitValueId + "\", \"limitValue\": \"" + limitValue + "\", \"attributeId\": \"" + attributeId + "\"}";
	}
}
