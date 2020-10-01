package com.ecpay.esafebox.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
public class BoxDto {
    Long boxId;
    Long boxSerial;
    String boxStatus;
    Long boxTypeId;
    String manufactureCode;
    @JsonFormat(pattern = "yyyyMMdd", shape = JsonFormat.Shape.STRING, 	timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime createdDate;

}
