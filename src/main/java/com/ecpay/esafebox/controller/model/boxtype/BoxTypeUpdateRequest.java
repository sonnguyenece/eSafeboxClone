package com.ecpay.esafebox.controller.model.boxtype;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class BoxTypeUpdateRequest extends BoxTypeCreateRequest{
	@ApiModelProperty(dataType = "Long", example = "1", notes = "Boxtype's id", required = true)
	private String boxTypeId;
}
