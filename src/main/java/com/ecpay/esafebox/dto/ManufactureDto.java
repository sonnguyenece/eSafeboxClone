package com.ecpay.esafebox.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManufactureDto {
	Long manufactureId;
    String manufactureCode;
    Long boxtypeId;
//    String boxId;
    String factureStatus;
    @JsonFormat(pattern = "yyyyMMdd", shape = JsonFormat.Shape.STRING, 	timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime createdDate;
	Long requestQuantity;
	Long issueQuantity;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	List<BoxDto> boxList;// = Collections.emptyList();
}
