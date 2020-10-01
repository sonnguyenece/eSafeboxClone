package com.ecpay.esafebox.mapper;

import java.util.List;

import com.ecpay.entities.ecbox.TbSet;
import com.ecpay.esafebox.dto.SetDto;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
@MapperConfig(implementationPackage = "com.ecpay.esafebox.mapper")
public interface SetMapper {
    @Mappings({
            @Mapping(target = "setId", source = "set.id"),
            @Mapping(target = "setCode", source = "set.code"),
            @Mapping(target = "setName", source = "set.name"),
    })
    SetDto toSetDto(TbSet set);
    List<SetDto> toSetDtos(List<TbSet> sets);
}
