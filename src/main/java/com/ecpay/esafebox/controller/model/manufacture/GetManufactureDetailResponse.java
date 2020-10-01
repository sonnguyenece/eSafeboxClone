package com.ecpay.esafebox.controller.model.manufacture;

import java.util.Set;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.entities.ecbox.Manufacture;
import com.ecpay.esafebox.controller.model.PagingResponse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GetManufactureDetailResponse extends ResponseMessage {
	private static final long serialVersionUID = 1659201017466839339L;
	@ApiModelProperty(dataType = "String", required = true, example = "0000")
	private String responseCode;
	@ApiModelProperty(dataType = "String", required = true, example = "Successful")
	private String responseMessage;
	@ApiModelProperty(dataType = "Object", required = true, example = "Response data")
	private ManufactureDetailResponseData responseData;
	@ApiModelProperty(dataType = "Long", required = true, example = "Processed time")
	private Long responseTime;

}

@Getter
@Setter
class ManufactureDetailResponseData extends PagingResponse {
	private Set<Manufacture> manufacture;
}
