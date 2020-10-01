package com.ecpay.esafebox.controller.model.set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class SetUpdateRequest extends SetCreateRequest {

    @ApiModelProperty(dataType = "Long", example = "1", notes = "Set's id", required = true)
    private String setId;

}
