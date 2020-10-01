package com.ecpay.esafebox.controller.model.manufacture;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListManufactureRequest extends PagingRequest {
	@ApiModelProperty(dataType = "String", example = "MEUCIQCGQ", notes = "manufacture code")
	private String manufactureCode;

	@ApiModelProperty(dataType = "Long", example = "1", notes = "boxTypeId")
	private String boxTypeId;

	@ApiModelProperty(dataType = "String", example = "P", notes = "factureStatus")
	private String factureStatus;

//	@ApiModelProperty(dataType = "String", example = "N", notes = "boxStatus")
//	private String boxStatus;

	@ApiModelProperty(dataType = "LocalDate", example = "20200903", notes = "fromDate")
	private String fromDate;

	@ApiModelProperty(dataType = "LocalDate", example = "20200904", notes = "toDate")
	private String toDate;
}
