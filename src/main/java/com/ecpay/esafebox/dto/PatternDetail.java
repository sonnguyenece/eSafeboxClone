package com.ecpay.esafebox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatternDetail {

	String pattern;

	String description;

	String errorMessage;
}
