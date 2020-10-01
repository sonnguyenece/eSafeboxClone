package com.ecpay.esafebox.controller.model.attribute;

import com.ecpay.esafebox.controller.model.PagingRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AttributeListRequest extends PagingRequest {
    @ApiModelProperty(dataType = "Long", example = "1", notes = "Attribute's id", required = true)
    private String attributeId;

    @ApiModelProperty(dataType = "String", example = "AAA", notes = "Attribute's code", required = true)
    private String attributeCode;

    @ApiModelProperty(dataType = "String", example = "aa", notes = "Attribute's name", required = true)
    private String attributeName;

    @ApiModelProperty(dataType = "Long", example = "1", notes = "Attribute's uomId", required = true)
    private String uomId;

    @ApiModelProperty(dataType = "Long", example = "1", notes = "Attribute's setId", required = true)
    private String setId;

}
