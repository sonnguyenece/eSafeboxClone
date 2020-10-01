package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Box;
import com.ecpay.esafebox.dto.BoxDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface BoxMapper {
	@Mappings({
		@Mapping(target = "boxId", source = "box.id"),
		@Mapping(target = "boxSerial", source = "box.serial"),
		@Mapping(target = "boxStatus", source = "box.status"),
		@Mapping(target = "boxTypeId", source = "box.manufacture.boxtypeId"),
		@Mapping(target = "manufactureCode", source = "box.manufacture.code"),
		@Mapping(target = "createdDate", source = "box.created"),
	})
	BoxDto toBoxDto(Box box);
	List<BoxDto> toBoxDtos(List<Box> boxs);
}
