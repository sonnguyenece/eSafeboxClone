package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.UomType;
import com.ecpay.esafebox.dto.UomTypeDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface UomTypeMapper {
	@Mappings({
		@Mapping(target = "uomTypeId", source = "uomType.id"),
		@Mapping(target = "uomTypeCode", source = "uomType.code"),
		@Mapping(target = "uomTypeName", source = "uomType.name"),
		@Mapping(target = "parentId", source = "uomType.parentId")
	})
	UomTypeDto toUomTypeDto(UomType uomType);
	List<UomTypeDto> toUomTypeDtos(List<UomType> uomTypes);
}
