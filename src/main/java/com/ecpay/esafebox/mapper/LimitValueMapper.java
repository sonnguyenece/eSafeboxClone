package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.LimitValue;
import com.ecpay.esafebox.dto.LimitValueDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface LimitValueMapper {
	@Mappings({
		@Mapping(target = "limitValueId", source = "limitValue.id"),
		@Mapping(target = "limitId", source = "limitValue.limitId"),
		@Mapping(target = "limitValue", source = "limitValue.value"),
		@Mapping(target = "attributeId", source = "limitValue.attributeId"),
	})
	LimitValueDto toLimitValueDto(LimitValue limitValue);
	List<LimitValueDto> toLimitValueDtos(List<LimitValue> limitValues);
}
