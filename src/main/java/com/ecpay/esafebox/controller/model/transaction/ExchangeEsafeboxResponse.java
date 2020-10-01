package com.ecpay.esafebox.controller.model.transaction;


import com.ecpay.entities.common.ResponseMessage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class ExchangeEsafeboxResponse extends ResponseMessage {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(dataType = "String", required = true, example = "0000")
	private String responseCode;
	@ApiModelProperty(dataType = "String", required = true, example = "Successful")
	private String responseMessage;
	@ApiModelProperty(dataType = "Object", required = true, example = "Response data")
	private ExchangeEsafeboxResponseData responseData;
	@ApiModelProperty(dataType = "Long", required = true, example = "Processed time")
	private Long responseTime;
	
	
}

@Getter
@Setter
class ExchangeEsafeboxResponseData {
	@ApiModelProperty(dataType = "String", required = true, example = "ead11638-f7fc-11ea-adc1-0242ac120002", notes = "UserBox Alias")
	String aliasBox;
	
	@ApiModelProperty(dataType = "String", required = true, example = "Base64String", notes = "UserBox KP")
	String kpBox;
}
