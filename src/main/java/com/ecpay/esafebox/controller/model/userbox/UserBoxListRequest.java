package com.ecpay.esafebox.controller.model.userbox;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class UserBoxListRequest extends PagingRequest{
	@ApiModelProperty(dataType = "Long", example = "123456", notes = "User's id (Authority)")
	private String userId;
	
	@ApiModelProperty(dataType = "Long", example = "123456789", notes = "Box Type Id")
	private String boxTypeId;
	
	@ApiModelProperty(dataType = "Long", example = "number", notes = "Box serial")
	private String boxSerial;
	
	@ApiModelProperty(dataType = "Date", example = "\"20200822\"", notes = "UserBox created date (yyyyMMdd)", required = true)
	private String fromDate;
	
	@ApiModelProperty(dataType = "Date", example = "\"20200822\"", notes = "UserBox created date (yyyyMMdd)", required = true)
	private String toDate;
	
}
