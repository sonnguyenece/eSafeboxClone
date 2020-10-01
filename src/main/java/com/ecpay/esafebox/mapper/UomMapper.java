package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Uom;
import com.ecpay.esafebox.dto.UomDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface UomMapper {
	@Mappings({ @Mapping(target = "uomId", source = "uom.id"),
			@Mapping(target = "uomTypeId", source = "uom.uomTypeId"),
			@Mapping(target = "uomAbbreviation", source = "uom.abbreviation"),
			@Mapping(target = "uomName", source = "uom.name") })
	UomDto toUomDto(Uom uom);

	List<UomDto> toUomDtos(List<Uom> uoms);
}
