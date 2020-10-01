package com.ecpay.esafebox.dto;

import com.ecpay.entities.ecbox.enumeration.AttributeDataType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttributeDto {
    Long attributeId;
    String attributeCode;
    String attributeName;
    AttributeDataType attributeType;
    String attributeFormat;
    Long uomId;
    String uomAbbreviation;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AttributeSetDto> attributeSetList;
}