package com.ecpay.esafebox.mapper;

import com.ecpay.entities.ecbox.AttributeSet;
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
public interface AttributeSetMapper {
    @Mappings({
            @Mapping(target = "attributeSetId", source = "attributeSet.id"),
            @Mapping(target = "attributeId", source = "attributeSet.attributeId"),
            @Mapping(target = "setId", source = "attributeSet.setId"),
            @Mapping(target = "setName", source = "attributeSet.set.name"),
    })
    AttributeSetDto toAttributeSetDto(AttributeSet attributeSet);
    List<AttributeSetDto> toAttributeSetDtos(List<AttributeSet> attributeSets);
}
