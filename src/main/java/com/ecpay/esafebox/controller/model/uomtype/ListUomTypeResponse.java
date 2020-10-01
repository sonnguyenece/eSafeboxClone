package com.ecpay.esafebox.controller.model.uomtype;

import java.util.Set;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.PagingResponse;
import com.ecpay.esafebox.dto.UomTypeDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel
public class ListUomTypeResponse extends ResponseMessage{
	private static final long serialVersionUID = -6955577418068548646L;
	@ApiModelProperty(dataType = "String", required = true, example ="0000")
	private String responseCode;
	@ApiModelProperty(dataType = "String", required = true, example ="Successful")
	private String responseMessage;
	@ApiModelProperty(dataType = "Object", required = true, example ="Response data")
	private ListUomTypeResponseData responseData;
	@ApiModelProperty(dataType = "Long", required = true, example ="Processed time")
	private Long responseTime;
	
}

@Getter
@Setter
class ListUomTypeResponseData extends PagingResponse{
	private Set<UomTypeDto> uomTypeList;
}
