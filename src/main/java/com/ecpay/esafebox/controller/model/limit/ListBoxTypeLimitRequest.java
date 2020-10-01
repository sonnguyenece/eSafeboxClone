package com.ecpay.esafebox.controller.model.limit;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListBoxTypeLimitRequest extends PagingRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "limit id")
	private String limitId;
	
	@ApiModelProperty(dataType = "String", example = "DPS_SD", notes = "limit code")
	private String limitCode;
	
	@ApiModelProperty(dataType = "String", example = "HẠN MỨC GỬI", notes = "limit name")
	private String limitName;
	
	@ApiModelProperty(dataType = "Long", example = "1", notes = "limit type")
	private String limitType;
	
	@ApiModelProperty(dataType = "Long", example = "1", notes = "box type id")
	private String boxTypeId;
}
