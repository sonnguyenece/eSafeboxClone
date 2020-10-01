package com.ecpay.esafebox.controller.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class BaseRequest {
	@ApiModelProperty(dataType = "Long", notes = "Unique AuditNumber", example = "987456123457891")
	Long auditNumber;
}
