package com.ecpay.esafebox.controller.model.manufacture;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GetManufactureDetailRequest extends PagingRequest {
	@ApiModelProperty(dataType = "Long", example = "1", notes = "manufacture id")
	private String manufactureId;

	@Override
	public String toString() {
		return "GetManufactureDetailRequest {\"manufactureId\": \"" + manufactureId + "\"}";
	}

}
