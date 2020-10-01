package com.ecpay.esafebox.controller.model.set;

import com.ecpay.esafebox.controller.model.PagingRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ListSetRequest extends PagingRequest {
    @ApiModelProperty(dataType = "Long", example = "1", notes = "Set's id", required = true)
    private String setId;

    @ApiModelProperty(dataType = "String", example = "AAA", notes = "Set's code", required = true)
    private String setCode;

    @ApiModelProperty(dataType = "String", example = "aA", notes = "Set's name", required = true)
    private String setName;
}
