package com.ecpay.esafebox.mapper;

import java.util.List;

import com.ecpay.esafebox.dto.LimitRemoveReponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.Limit;
import com.ecpay.entities.ecbox.LimitValue;
import com.ecpay.esafebox.dto.LimitCreateResponseDto;
import com.ecpay.esafebox.dto.LimitDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface LimitMapper {
	
	@Mappings({
		@Mapping(target = "limitId", source = "limit.id"),
		@Mapping(target = "limitCode", source = "limit.code"),
		@Mapping(target = "limitName", source = "limit.name"),
		@Mapping(target = "limitType", source = "limit.type"),
		@Mapping(target = "boxTypeId", source = "limit.boxTypeId"),
		@Mapping(target = "listLimitValue", source = "limit.listLimitValue")
	})
	LimitDto toLimitDto(Limit limit);

	@Mappings({
			@Mapping(target = "limitId", source = "limit.id"),
			@Mapping(target = "listLimitValue", source = "limit.listLimitValue")
	})
	LimitRemoveReponseDto toLimitRemoveReponseDto(Limit limit);
	
	@Mappings({
		@Mapping(target = "limitId", source = "limit.id"),
		@Mapping(target = "limitCode", source = "limit.code"),
		@Mapping(target = "limitName", source = "limit.name"),
		@Mapping(target = "limitType", source = "limit.type"),
		@Mapping(target = "boxTypeId", source = "limit.boxTypeId"),
		@Mapping(target = "limitValueId", source = "limitValue.id"),
		@Mapping(target = "limitValue", source = "limitValue.value"),
		@Mapping(target = "attributeId", source = "limitValue.attributeId")
	})
	LimitCreateResponseDto toLimitCreateResponseDto(Limit limit, LimitValue limitValue);
	
	List<LimitDto> toLimitDtos(List<Limit> limits);
}
