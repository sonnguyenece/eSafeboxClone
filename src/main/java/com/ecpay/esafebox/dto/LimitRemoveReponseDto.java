package com.ecpay.esafebox.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitRemoveReponseDto {
    Long limitId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<LimitValueDto> listLimitValue;
}
