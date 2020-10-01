package com.ecpay.esafebox.controller.model.userpair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserPairCreateRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "user from", required = true)
	private String userPairUserFrom;

	@ApiModelProperty(dataType = "Long", example = "2", notes = "user to", required = true)
	private String userPairUserTo;

	@Override
	public String toString() {
		return "UserPairCreateRequest {\"userPairUserFrom\": \"" + userPairUserFrom + "\", \"userPairUserTo\": \"" + userPairUserTo + "\"}";
	}

}
