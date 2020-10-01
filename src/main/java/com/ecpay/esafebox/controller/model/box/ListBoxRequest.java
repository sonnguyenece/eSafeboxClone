package com.ecpay.esafebox.controller.model.box;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListBoxRequest extends PagingRequest{
	@ApiModelProperty(dataType = "Long", example = "string", notes = "Box Type", required = true)
	private String boxTypeId;
	
	@ApiModelProperty(dataType = "String", example = "string", notes = "Box Manufacture", required = true)
	private String manufactureCode;
	
	@ApiModelProperty(dataType = "Long", example = "number", notes = "Box serial", required = true)
	private String boxSerial;
	
	@ApiModelProperty(dataType = "String", example = "Y", notes = "Box status", required = true, allowableValues = "0|Y|N")
	private String boxStatus;
	
	@ApiModelProperty(dataType = "Date", example = "\"20200822\"", notes = "Box created date (yyyyMMdd)", required = true)
	private String fromDate;
	
	@ApiModelProperty(dataType = "Date", example = "\"20200822\"", notes = "Box created date (yyyyMMdd)", required = true)
	private String toDate;
}
