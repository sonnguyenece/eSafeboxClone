package com.ecpay.esafebox.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
public class TransactionItem {
	String issuerCode;
	List<String> values;
	List<Long> quantities;
}
