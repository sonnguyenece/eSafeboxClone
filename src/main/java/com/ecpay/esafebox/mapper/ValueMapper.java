package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Value;
import com.ecpay.esafebox.dto.ValueDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface ValueMapper {
	@Mappings({
		@Mapping(target = "attributeId", source = "value.attributeId"),
		@Mapping(target = "attributeValue", source = "value.genericValue")
	})
	ValueDto toValueDto(Value value);
	List<ValueDto> toValueDtos(List<Value> values);
}
