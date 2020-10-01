package com.ecpay.esafebox.controller.model.uomtype;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListUomTypeRequest extends PagingRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "UOM type id")
	private String uomTypeId;
	
	@ApiModelProperty(dataType = "String", example = "AAA", notes = "UOM type code")
	private String uomTypeCode;
	
	@ApiModelProperty(dataType = "String", example = "aA", notes = "UOM type name")
	private String uomTypeName;
}
