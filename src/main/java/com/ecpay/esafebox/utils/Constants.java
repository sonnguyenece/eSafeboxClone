package com.ecpay.esafebox.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class Constants {

	public static final String DATE_PATTERN = "yyyyMMdd";
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

	public static final String DATE_TIME_WITH_MILISECOND_PATTERN = "yyyyMMdd HH:mm:ss.SSS";
	public static final DateTimeFormatter DATE_TIME_WITH_MILISECOND_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_WITH_MILISECOND_PATTERN);

	public static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Response message
     */
    public static final String RESPONSE_CODE_FIELD = "responseCode";
    public static final String RESPONSE_MESSAGE_FIELD = "responseMessage";
    public static final String RESPONSE_DATA_FIELD = "responseData";
	public static final String RESPONSE_TIME = "responseTime";
    public static final String RESPONSE_CODE_SUCCESS_VALUE = "0000";
	public static final String RESPONSE_MESSAGE_SUCCESS_VALUE = "Success!!!";

	public static final long PAGE_SIZE = 50;
	public static final long PAGE_NUMBER = 1;
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	
	public static final String YES = "Y";
	public static final String NO = "N";
	
	public static final String SUCCESS = "S";
	public static final String FAIL = "F";
	
	public static final String Invalidcode = "0998";
	
    static {
        MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

	
	public interface LOGGER_APPENDER {
		public static final String APPLICATION = "application";
		public static final String API = "api";
		public static final String SERVICE = "service";
		public static final String DB = "db";
		public static final String COMMON = "common";
	}
}
