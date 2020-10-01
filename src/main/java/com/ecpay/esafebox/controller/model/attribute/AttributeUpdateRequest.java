package com.ecpay.esafebox.controller.model.attribute;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AttributeUpdateRequest extends AttributeCreateRequest {

    @ApiModelProperty(dataType = "Long", example = "1", notes = "Attribute's id", required = true)
    String AttributeId;
}
