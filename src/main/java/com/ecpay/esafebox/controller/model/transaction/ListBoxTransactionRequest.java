package com.ecpay.esafebox.controller.model.transaction;

import com.ecpay.esafebox.controller.model.PagingRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListBoxTransactionRequest extends PagingRequest {
	@ApiModelProperty(dataType = "Long", example = "0", notes = "sender", required = true)
	private String sender;
	
	@ApiModelProperty(dataType = "Long", example = "0", notes = "receiver", required = true)
	private String receiver;

	@ApiModelProperty(dataType = "String", example = "0", notes = "transaction type", required = true, allowableValues= "DS|CS|SS|SD|0")
	private String transactionType;

	@ApiModelProperty(dataType = "Date", example = "\"20200822\"", notes = "transaction from date (yyyyMMdd)", required = true)
	private String fromDate;

	@ApiModelProperty(dataType = "Date", example = "\"20200822\"", notes = "transaction to date (yyyyMMdd)", required = true)
	private String toDate;
}
