package com.ecpay.esafebox.controller.model.boxtype;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListBoxTypeRequest extends PagingRequest{
	
	@ApiModelProperty(dataType = "String", required = true, allowableValues = "asc|desc", example = "desc", notes = "Must be 'asc' or 'desc', default: asc")
	private String order;
	
	@ApiModelProperty(dataType = "String", example = "AAA", notes = "Box type code")
	private String boxTypeCode;
	
	@ApiModelProperty(dataType = "String", example = "aA", notes = "Box type name")
	private String boxTypeName;
	
	@ApiModelProperty(dataType = "Long", example = "1000000", notes = "Box type price")
	private String boxTypePrice;
	
	@ApiModelProperty(dataType = "Long", example = "2500000", notes = "Box type sale")
	private String boxTypeSale;
	
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Box type Attributes Set Id")
	private String setId;
	
}
