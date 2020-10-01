package com.ecpay.esafebox.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Date;

import static com.ecpay.entities.common.EcConstants.DATE_FORMATTER;
import static com.ecpay.entities.common.EcConstants.DATE_TIME_WITHOUT_MILISECOND_FORMATTER;

@Slf4j
@UtilityClass
public class TimeUtils {

    public LocalDateTime now() {
        return LocalDateTime.now(LocaleContextHolder.getTimeZone().toZoneId());
    }

    public ZonedDateTime nowZoned() {
        return ZonedDateTime.now(LocaleContextHolder.getTimeZone().toZoneId());
    }

    public ZonedDateTime toZonedDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(LocaleContextHolder.getTimeZone().toZoneId());
    }

	public LocalDateTime getStartOfDateTime(LocalDate startDate) {
		ZonedDateTime zonedDateTime =  (startDate == null) ? null : startDate.atStartOfDay().atZone(LocaleContextHolder.getTimeZone().toZoneId());
		if (zonedDateTime != null) {
			return zonedDateTime.toLocalDateTime();
		}

		return null;
	}

	public LocalDateTime getEndOfDateTime(LocalDate endDate) {
		ZonedDateTime zonedDateTime = (endDate == null) ? null : LocalDateTime.of(endDate, LocalTime.of(23, 59, 59, 999))
				.atZone(LocaleContextHolder.getTimeZone().toZoneId());

		if (zonedDateTime != null) {
			return zonedDateTime.toLocalDateTime();
		}

		return null;
	}

	public LocalDate convertString2LocalDate(Object dateObject) {
    	if (dateObject == null || StringUtils.isEmpty(dateObject.toString())) {
    		return null;
		}

    	try {
			return LocalDate.parse(dateObject.toString(), DATE_FORMATTER);
		} catch (DateTimeParseException dtpe) {
    		log.warn("Can not parse [{}] to LocalDate. Reason: {}", dateObject.toString(), dtpe.getLocalizedMessage());
    		return null;
		}
	}

	public LocalDate convertString2LocalDateTime(Object dateObject) {
		if (dateObject == null || StringUtils.isEmpty(dateObject.toString())) {
			return null;
		}

		try {
			return LocalDate.parse(dateObject.toString(), DATE_TIME_WITHOUT_MILISECOND_FORMATTER);
		} catch (DateTimeParseException dtpe) {
			log.warn("Can not parse [{}] to LocalDate. Reason: {}", dateObject.toString(), dtpe.getLocalizedMessage());
			return null;
		}
	}

	public String convertLocalDate2String(LocalDate localDate) {
		if (localDate == null) {
			return null;
		}

		try {
			return DATE_FORMATTER.format(localDate);
		} catch (DateTimeException dtpe) {
			log.warn("Can not format [{}] to String. Reason: {}", localDate, dtpe.getLocalizedMessage());
			return null;
		}
	}

	public String convertLocalDateTime2String(LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return null;
		}

		try {
			return DATE_TIME_WITHOUT_MILISECOND_FORMATTER.format(localDateTime);
		} catch (DateTimeException dtpe) {
			log.warn("Can not format [{}] to String. Reason: {}", localDateTime, dtpe.getLocalizedMessage());
			return null;
		}
	}
}
