package com.ecpay.esafebox.controller.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class PagingRequest {
	@ApiModelProperty(dataType = "Long", required = true, example = "1", notes = "Page number")
	private String pageNumber;
	
	@ApiModelProperty(dataType = "Long", required = true, example = "50", notes = "Page size")
	private String pageSize;
	
	@ApiModelProperty(dataType = "Array", required = true, example = "[]")
	private Object sorts;
	
	@ApiModelProperty(dataType = "String", required = true, allowableValues = "asc|desc", example = "asc", notes = "Must be 'asc' or 'desc', default: asc")
	private String order;
}
