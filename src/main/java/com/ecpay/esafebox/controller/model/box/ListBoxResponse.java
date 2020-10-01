package com.ecpay.esafebox.controller.model.box;

import java.util.Set;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.PagingResponse;
import com.ecpay.esafebox.dto.BoxDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel
public class ListBoxResponse extends ResponseMessage{
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(dataType = "String", required = true, example ="0000")
	private String responseCode;
	@ApiModelProperty(dataType = "String", required = true, example ="Successful")
	private String responseMessage;
	@ApiModelProperty(dataType = "Object", required = true, example ="Response data")
	private ListBoxResponseData responseData;
	@ApiModelProperty(dataType = "Long", required = true, example ="Processed time")
	private Long responseTime;
	
}

@Getter
@Setter
class ListBoxResponseData extends PagingResponse{
	private Set<BoxDto> boxList;
}
