package com.ecpay.esafebox.exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.ecpay.entities.common.ResponseMessage;
import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomApiError extends ResponseMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> errors;
	
	public CustomApiError(final String responseCode, final String description, final List<String> errors) {
        setResponseCode(responseCode);
        setResponseMessage(description);
        setErrors(errors);
        setResponseTime(System.currentTimeMillis());
    }

    public CustomApiError(final String responseCode, final String description, final String error) {
        setResponseCode(responseCode);
        setResponseMessage(description);
        setError(error);
        setResponseTime(System.currentTimeMillis());
    }

    public CustomApiError(final HttpStatus httpStatus, final List<String> errors) {
        setResponseCode(String.valueOf(httpStatus.value()));
        setResponseMessage(httpStatus.getReasonPhrase());
        setErrors(errors);
        setResponseTime(System.currentTimeMillis());
    }

    public CustomApiError(final HttpStatus httpStatus, final String error) {
        setResponseCode(String.valueOf(httpStatus.value()));
        setResponseMessage(httpStatus.getReasonPhrase());
        setError(error);
        setResponseTime(System.currentTimeMillis());
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(final List<String> errors) {
        this.errors = errors;
    }

    public void setError(final String error) {
        errors = Arrays.asList(error);
    }
}
