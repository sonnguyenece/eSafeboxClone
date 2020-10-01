package com.ecpay.esafebox.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ESafeboxException extends Exception {
	private static final long serialVersionUID = 1L;
	private String reason;
	
	public ESafeboxException(String code) {
		super(code);
	}

	@Builder
	public ESafeboxException(String code, String reason) {
		super(code);
		this.reason = reason;
	}
	
}
