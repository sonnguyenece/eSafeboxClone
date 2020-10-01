package com.ecpay.esafebox.utils;

import com.querydsl.core.types.dsl.*;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
@Slf4j
public class PredicateSearchUtils {

	public BooleanExpression inExpression(EnumPath enumPath, List<?> values) {
		return CollectionUtils.isEmpty(values) ? null : enumPath.in(values);
	}

	public BooleanExpression inExpression(NumberPath<Long> numberPath, List<Long> values) {
		return CollectionUtils.isEmpty(values) ? null : numberPath.in(values);
	}

	public <T> BooleanExpression eqExpression(SimpleExpression<T> expression, T value) {
		return value == null ? null : expression.eq(value);
	}

	public BooleanExpression eqExpression(NumberPath<Long> numberPath, Long	value) { 
		return value == null ? null : numberPath.eq(value);
	}
	
	public BooleanExpression ltExpression(NumberPath<Long> numberPath, Long	value) { 
		return value == null ? null : numberPath.lt(value);
	}
	
	public BooleanExpression gtExpression(NumberPath<Long> numberPath, Long	value) { 
		return value == null ? null : numberPath.gt(value);
	}

	public BooleanExpression neExpression(NumberPath<Long> numberPath, Long	value) { 
		return value == null ? null : numberPath.ne(value);
	}
	
	public BooleanExpression eqExpression(StringPath stringPath, String	value) { 
		return value == null ? null : stringPath.eq(value);
	}

	
	public BooleanExpression containsExpression(StringPath stringPath, String value) {
		return value == null ? null : stringPath.containsIgnoreCase(value);
	}

	public BooleanExpression betweenExpression(
			DateTimePath dateTimePath, LocalDateTime fromDate, LocalDateTime toDate) {
		if (fromDate != null && toDate == null) {
			return dateTimePath.after(fromDate);
		} else if (fromDate == null && toDate != null) {
			return dateTimePath.before(toDate);
		} else if (fromDate != null) {
			return dateTimePath.between(fromDate, toDate);
		}

		return null;
	}

	public BooleanExpression betweenExpression(
			DateTimePath dateTimePath, String fromDate, String toDate) {
		if (!StringUtils.isEmpty(fromDate) && StringUtils.isEmpty(toDate)) {
			return dateTimePath.after(TimeUtils.getStartOfDateTime(TimeUtils.convertString2LocalDate(fromDate)));
		} else if (StringUtils.isEmpty(fromDate) && !StringUtils.isEmpty(toDate)) {
			return dateTimePath.before(TimeUtils.getEndOfDateTime(TimeUtils.convertString2LocalDate(toDate)));
		} else if (!StringUtils.isEmpty(fromDate)) {
			return dateTimePath.between(
					TimeUtils.getStartOfDateTime(TimeUtils.convertString2LocalDate(fromDate)),
					TimeUtils.getEndOfDateTime(TimeUtils.convertString2LocalDate(toDate)));
		}

		return null;
	}

	public BooleanExpression beforeExpression(
			DateTimePath dateTimePath, LocalDate toDate) {
		if (toDate != null) {
			return dateTimePath.before(TimeUtils.getEndOfDateTime(toDate));
		}

		return null;
	}

	public BooleanExpression beforeOrEqualExpression(
			DateTimePath dateTimePath, LocalDate toDate) {
		if (toDate != null) {
			return (dateTimePath.loe(TimeUtils.getEndOfDateTime(toDate))) ;
		}

		return null;
	}

	public BooleanExpression afterExpression(
			DateTimePath dateTimePath, LocalDate toDate) {
		if (toDate != null) {
			return dateTimePath.after(TimeUtils.getEndOfDateTime(toDate));
		}

		return null;
	}
	
	public BooleanExpression eqExpression(NumberPath<Integer> numberPath, NumberPath<Integer> right) { 
		return numberPath.eq(right);
	}
	
	public BooleanExpression eqLongExpression(NumberPath<Long> numberPath, NumberPath<Long> right) { 
		return numberPath.eq(right);
	}
	
}
