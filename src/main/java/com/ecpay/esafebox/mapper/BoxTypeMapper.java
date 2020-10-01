package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Boxtype;
import com.ecpay.esafebox.dto.BoxTypeDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface BoxTypeMapper {
	@Mappings({
		@Mapping(target = "boxTypeId", source = "boxType.id"),
		@Mapping(target = "boxTypeCode", source = "boxType.code"),
		@Mapping(target = "boxTypeName", source = "boxType.name"),
		@Mapping(target = "boxTypePrice", source = "boxType.price"),
		@Mapping(target = "boxTypeSale", source = "boxType.sale"),
		@Mapping(target = "setId", source = "boxType.setId"),
		@Mapping(target = "setName", source = "boxType.set.name"),
		@Mapping(target = "boxTypeIcon", expression = "java(boxType.getIcon() == null ? null : new String(boxType.getIcon()))")
	})
	BoxTypeDto toBoxTypeDto(Boxtype boxType);
	List<BoxTypeDto> toBoxTypeDtos(List<Boxtype> boxTypes);
}
