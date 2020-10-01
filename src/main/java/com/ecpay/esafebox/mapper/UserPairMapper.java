package com.ecpay.esafebox.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import com.ecpay.entities.ecbox.UserPair;
import com.ecpay.esafebox.dto.UserPairDto;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface UserPairMapper {
	@Mappings({ @Mapping(target = "userPairId", source = "userPair.id"),
			@Mapping(target = "userPairUserFrom", source = "userPair.userFrom"),
			@Mapping(target = "userPairUserTo", source = "userPair.userTo"),
			@Mapping(target = "userPairStatus", source = "userPair.status") })
	UserPairDto toUserPairDto(UserPair userPair);

	List<UserPairDto> toUserPairDtos(List<UserPair> userPairs);
}
