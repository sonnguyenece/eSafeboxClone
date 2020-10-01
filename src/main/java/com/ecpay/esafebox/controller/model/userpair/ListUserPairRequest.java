package com.ecpay.esafebox.controller.model.userpair;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListUserPairRequest extends PagingRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "user from")
	private String userPairUserFrom;
	
	@ApiModelProperty(dataType = "Long", example = "1", notes = "user to")
	private String userPairUserTo;
	
	@ApiModelProperty(dataType = "String", example = "Y", notes = "user's status")
	private String userPairStatus;
}
