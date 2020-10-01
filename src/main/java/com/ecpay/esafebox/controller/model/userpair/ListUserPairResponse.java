package com.ecpay.esafebox.controller.model.userpair;

import java.util.Set;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.PagingResponse;
import com.ecpay.esafebox.dto.UserPairDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListUserPairResponse extends ResponseMessage {
	private static final long serialVersionUID = -5215208511605540331L;
	@ApiModelProperty(dataType = "String", required = true, example = "0000")
	private String responseCode;
	@ApiModelProperty(dataType = "String", required = true, example = "Successful")
	private String responseMessage;
	@ApiModelProperty(dataType = "Object", required = true, example = "Response data")
	private ListUserPairResponseData responseData;
	@ApiModelProperty(dataType = "Long", required = true, example = "Processed time")
	private Long responseTime;

}

@Getter
@Setter
class ListUserPairResponseData extends PagingResponse {
	private Set<UserPairDto> userPairList;
}
