package com.ecpay.esafebox.controller.model.userpair;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserPairUpdateRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "user from", required = true)
	private String userPairUserFrom;

	@ApiModelProperty(dataType = "Array", example = "[1,2]", notes = "user to list", required = true)
	private Object userToList;
}
