package com.ecpay.esafebox.controller.model.attribute;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class AttributeCrudResponse {
    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's id", required = true)
    private String attributeId;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's code", required = true)
    private String attributeCode;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's name", required = true)
    private String attributeName;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's type", required = true)
    private String attributeType;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's format", required = true)
    private String attributeFormat;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's uomId", required = true)
    private String uomId;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's attributeSetId", required = true)
    private String attributeSetId;

    @ApiModelProperty(dataType = "String", example = "string", notes = "Attribute's setId", required = true)
    private String setId;

    @Override
    public String toString() {
        return "AttributeCrudResponse{" +
                "attributeId='" + attributeId + '\'' +
                ", attributeCode='" + attributeCode + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", attributeType='" + attributeType + '\'' +
                ", attributeFormat='" + attributeFormat + '\'' +
                ", uomId='" + uomId + '\'' +
                ", attributeSetId='" + attributeSetId + '\'' +
                ", setId='" + setId + '\'' +
                '}';
    }
}
