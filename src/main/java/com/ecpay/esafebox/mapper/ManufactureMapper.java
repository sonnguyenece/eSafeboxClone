package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Manufacture;
import com.ecpay.esafebox.dto.ManufactureDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface ManufactureMapper {
	@Mappings({
		@Mapping(target = "manufactureId", source = "manufacture.id"),
		@Mapping(target = "manufactureCode", source = "manufacture.code"),
		@Mapping(target = "boxtypeId", source = "manufacture.boxtypeId"),
		@Mapping(target = "factureStatus", source = "manufacture.status"),
		@Mapping(target = "createdDate", source = "manufacture.created"),
		@Mapping(target = "requestQuantity", source = "manufacture.quantity"),
		@Mapping(target = "issueQuantity", expression = "java(manufacture.getListBox() == null ? null : Long.valueOf(manufacture.getListBox().size()))"),
	})
	ManufactureDto toManufactureDto(Manufacture manufacture);
	List<ManufactureDto> toManufactureDtos(List<Manufacture> manufactures);
}
