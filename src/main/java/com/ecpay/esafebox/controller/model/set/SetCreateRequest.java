package com.ecpay.esafebox.controller.model.set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SetCreateRequest {

    @ApiModelProperty(dataType = "String", example = "string", notes = "Set's code", required = true)
    private String setCode;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Set's name", required = true)
    private String setName;

    @Override
    public String toString() {
        return "SetCreateRequest{" +
                "setCode='" + setCode + '\'' +
                ", setName='" + setName + '\'' +
                '}';
    }

}
