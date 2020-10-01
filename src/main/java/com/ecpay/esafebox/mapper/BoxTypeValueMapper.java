package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Value;
import com.ecpay.esafebox.dto.BoxtypeValueDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface BoxTypeValueMapper {
	@Mappings({
		@Mapping(target = "valueId", source = "boxTypeValue.id"),
		@Mapping(target = "boxTypeId", source = "boxTypeValue.boxTypeId"),
		@Mapping(target = "attributeId", source = "boxTypeValue.attributeId"),
		@Mapping(target = "attributeValue", source = "boxTypeValue.genericValue")
	})
	BoxtypeValueDto toBoxTypeValueDto(Value boxTypeValue);
	List<BoxtypeValueDto> toBoxTypeValueDtos(List<Value> boxTypeValues);
}
