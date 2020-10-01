package com.ecpay.esafebox.dto;

import com.ecpay.entities.ecbox.Attribute;
import com.ecpay.entities.ecbox.TbSet;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)

public class AttributeSetDto {
    Long attributeSetId;
    Long attributeId;
    Long setId;
    String setName;

}
