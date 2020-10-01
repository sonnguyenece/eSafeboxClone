package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.UserBox;
import com.ecpay.esafebox.dto.UserBoxDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface UserBoxMapper {
//	@Mappings({
//		@Mapping(target = "userBoxId", source = "userBox.id"),
//		@Mapping(target = "userId", source = "userBox.userId"),
//		@Mapping(target = "boxId", source = "userBox.boxId"),
//		@Mapping(target = "boxSerial", source = "userBox.box.serial"),
//		@Mapping(target = "userBoxKp", expression = "java(userBox.getKp() == null ? null : java.util.Base64.getEncoder().encodeToString(javax.xml.bind.DatatypeConverter.parseHexBinary(userBox.getKp())))"),
//		@Mapping(target = "regiDate", source = "userBox.regiDate"),
//	})
//	UserBoxDto toUserBoxDto(UserBox userBox);
	
	
	@Mappings({
		@Mapping(target = "userBoxId", source = "userBox.id"),
		@Mapping(target = "userId", source = "userBox.userId"),
		@Mapping(target = "boxId", source = "userBox.boxId"),
		@Mapping(target = "boxSerial", source = "userBox.box.serial"),
		@Mapping(target = "userBoxKp", expression = "java(userBox.getBoxKp() == null ? null : java.util.Base64.getEncoder().encodeToString(javax.xml.bind.DatatypeConverter.parseHexBinary(userBox.getBoxKp())))"),
		@Mapping(target = "keyBox", expression = "java(userBox.getBoxKey() == null ? null : java.util.Base64.getEncoder().encodeToString(userBox.getBoxKey().getBytes()))"),
		@Mapping(target = "regiDate", source = "userBox.regiDate"),
	})
	UserBoxDto toUserBoxDto(UserBox userBox);
	
	List<UserBoxDto> toUserBoxDtos(List<UserBox> userBoxs);
}
