package com.ecpay.esafebox.controller.model.userbox;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ActiveUserBoxRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "userId", required = true)
	private String userId;

	@ApiModelProperty(dataType = "String", example = "CDFSF", notes = "terminalId")
	private String terminalId;

	@ApiModelProperty(dataType = "String", example = "string", notes = "terminal info")
	private String terminalInfo;

	@ApiModelProperty(dataType = "String", example = "string", notes = "box type code")
	private String boxTypeCode;

	@Override
	public String toString() {
		return "ActiveUserBoxRequest {\"userId\": \"" + userId + "\", \"terminalId\": \"" + terminalId
				+ "\", \"terminalInfo\": \"" + terminalInfo + "\", \"boxTypeCode\": \"" + boxTypeCode + "\"}";
	}

}
