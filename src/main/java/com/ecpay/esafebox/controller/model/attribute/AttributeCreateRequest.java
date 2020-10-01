package com.ecpay.esafebox.controller.model.attribute;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AttributeCreateRequest {
    @ApiModelProperty(dataType = "String", example = "codeTest", notes = "Attribute's code", required = true)
    private String attributeCode;

    @ApiModelProperty(dataType = "String", example = "nameTest", notes = "Attribute's name", required = true)
    private String attributeName;

    @ApiModelProperty(dataType = "String", example = "DATE", notes = "Attribute's type", required = true)
    private String attributeType;

    @ApiModelProperty(dataType = "String", example = "YYYY-MM-DD", notes = "Attribute's format", required = true)
    private String attributeFormat;

    @ApiModelProperty(dataType = "Long", example = "1", notes = "Attribute's uomId", required = true)
    private String uomId;

    @ApiModelProperty(dataType = "Long", example = "1", notes = "Attribute's setId", required = true)
    private String setId;

    @Override
    public String toString() {
        return "AttributeCreateRequest{" +
                "attributeCode='" + attributeCode + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", attributeType='" + attributeType + '\'' +
                ", attributeFormat='" + attributeFormat + '\'' +
                ", uomId='" + uomId + '\'' +
                ", setId='" + setId + '\'' +
                '}';
    }
}
