package com.ecpay.esafebox.controller.model.limit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class BoxTypeLimitRemoveRequest {
    @ApiModelProperty(dataType = "Long", example = "1", notes = "limit Id")
    private String limitId;

    @Override
    public String toString() {
        return "BoxTypeLimitRemoveRequest {\"limitId\": \"" + limitId + "\"}";
    }
}
