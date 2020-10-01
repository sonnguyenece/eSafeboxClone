package com.ecpay.esafebox.controller.model.uom;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListUomRequest extends PagingRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "UOM id")
	private String uomId;
	
	@ApiModelProperty(dataType = "Long", example = "1", notes = "UOM type id")
	private String uomTypeId;
	
	@ApiModelProperty(dataType = "String", example = "AAA", notes = "UOM abbreviation")
	private String uomAbbreviation;
	
	@ApiModelProperty(dataType = "String", example = "aA", notes = "UOM name")
	private String uomName;
}
