package com.ecpay.esafebox.controller.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class PagingResponse{
	@ApiModelProperty(dataType = "Long", required = true, example ="Same as request pageNumber")
	private Long pageNumber;
	
	@ApiModelProperty(dataType = "Long", required = true, example = "Same as request pageSize")
	private Long pageSize;
	
	@ApiModelProperty(dataType = "Long", required = true, example = "Total elements")
	private Long totalElements;
	
	@ApiModelProperty(dataType = "Long", required = true, example = "Total pages")
	private Long totalPages;
	
	@ApiModelProperty(dataType = "Array", required = true, example = "Same as request orderBy")
	private Object sorts;
	
	@ApiModelProperty(dataType = "String", required = true, example = "Same as request orderBy")
	private String order;
}
