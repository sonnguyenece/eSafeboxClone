package com.ecpay.esafebox.mapper;

import com.ecpay.entities.ecbox.Attribute;
import com.ecpay.entities.ecbox.AttributeSet;
import com.ecpay.entities.ecbox.TbSet;
import com.ecpay.entities.ecbox.Uom;
import com.ecpay.esafebox.dto.AttributeDto;
import com.ecpay.esafebox.dto.AttributeSetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface AttributeMapper {
    @Mappings({
            @Mapping(target = "attributeId", source = "attribute.id"),
            @Mapping(target = "attributeCode", source = "attribute.code"),
            @Mapping(target = "attributeName", source = "attribute.name"),
            @Mapping(target = "attributeType", source = "attribute.type"),
            @Mapping(target = "attributeFormat", source = "attribute.format"),
            @Mapping(target = "uomId", source = "attribute.uomId"),
            @Mapping(target = "uomAbbreviation", source = "attribute.uom.abbreviation"),
            @Mapping(target = "attributeSetList", source = "attribute.listAttrSet"),

    })
    AttributeDto toAttributeDto(Attribute attribute );
    List<AttributeDto> toAttributeDtos(List<Attribute> attributes);

}
