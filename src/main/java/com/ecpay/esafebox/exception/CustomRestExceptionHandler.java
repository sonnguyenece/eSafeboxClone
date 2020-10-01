package com.ecpay.esafebox.exception;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.info(ex.getClass().getName());
        //
		final String error = "Unprocessable input data";
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
	}
	
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", errors);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(final BindException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", errors);
        return handleExceptionInternal(ex, apiError, headers, HttpStatus.OK, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(final TypeMismatchException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = ex.getRequestPartName() + " part is missing";
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = ex.getParameterName() + " parameter is missing";
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    //

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = ex.getName() + " should be of type " + ex.getRequiredType().getName();
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(final ConstraintViolationException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final List<String> errors = new ArrayList<String>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            //errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
            String path = violation.getPropertyPath().toString();
            int index = path.lastIndexOf(".");
            if (index != -1 ) {
                path = path.substring(index+1);
            }
            errors.add(path + ": " + violation.getMessage());
        }
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", errors);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    // 404

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        final CustomApiError apiError = new CustomApiError(HttpStatus.NOT_FOUND, error);
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    // 405

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        final CustomApiError apiError = new CustomApiError("0001", HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    // 415

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append("This media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        final CustomApiError apiError = new CustomApiError("0001", HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), builder.substring(0, builder.length() - 1));
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    // 500

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        logger.error("error", ex);
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getLocalizedMessage());
        final CustomApiError apiError = new CustomApiError("0001", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final  ResponseEntity<Object> exceptionHandlerIllegalArgumentException(final IllegalArgumentException ex) {
        logger.info(ex.getClass().getName());
        //
        final CustomApiError apiError = new CustomApiError("0001", "Invalid or Bad request", ex.getMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }
    
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
			ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    	logger.info(ex.getClass().getName());
        //
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getLocalizedMessage());
        final CustomApiError apiError = new CustomApiError("0001", HttpStatus.BAD_REQUEST.getReasonPhrase(), builder.toString());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), HttpStatus.OK);
    }
    
}
