package com.ecpay.esafebox.controller.model.userbox;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserBoxKeyUpdateRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "userId", required = true)
	private String userId;

	@ApiModelProperty(dataType = "Long", example = "1", notes = "boxId", required = true)
	private String boxId;

	@ApiModelProperty(dataType = "String", example = "string", notes = "Userbox Key's alias (return from eHSM)")
	private String alias;

	@ApiModelProperty(dataType = "String", example = "string", notes = "Userbox Key public (return from eHSM)")
	private String publicKey;
	
	@ApiModelProperty(dataType = "String", example = "string", notes = "Userbox Key algorithm (return from eHSM)")
	private String algorithm;

	@Override
	public String toString() {
		return "UserBoxKeyUpdateRequest {\"userId\": \"" + userId + "\", \"boxId\": \"" + boxId + "\", \"alias\": \""
				+ alias + "\", \"publicKey\": \"" + publicKey + "\", \"algorithm\": \"" + algorithm + "\"}";
	}

}
