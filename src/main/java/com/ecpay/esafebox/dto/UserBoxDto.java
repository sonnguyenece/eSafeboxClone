package com.ecpay.esafebox.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class UserBoxDto {
	Long userBoxId;
	Long userId;
	Long boxId;
    Long boxSerial;
    @JsonInclude(Include.NON_NULL)
    String userBoxKp;
    @JsonInclude(Include.NON_NULL)
    Object keyBox;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mi:ss", shape = JsonFormat.Shape.STRING, 	timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime regiDate;
}
