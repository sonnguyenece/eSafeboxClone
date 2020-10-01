package com.ecpay.esafebox.search;

import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ecpay.entities.ecbox.QManufacture;
import com.ecpay.esafebox.dto.enumeration.fieldname.ManufactureListFieldName;
import com.ecpay.esafebox.utils.EsafeboxUtils;
import com.ecpay.esafebox.utils.PredicateSearchUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class ManufactureSearchCriteria {

	private Map<String, Object> data;

	public Predicate buildPredicate() {
		List<Predicate> predicates = Lists.newArrayList();

    	QManufacture qManufacture = QManufacture.manufacture;
    	    			
		//manufactureCode
		String manufactureCode = EsafeboxUtils.getFieldValueAsString(ManufactureListFieldName.MANUFACTURE_CODE.getEsafeboxFieldName().getFieldName(), data);
		if (!StringUtils.isEmpty(manufactureCode)) {
			BooleanExpression manufactureCodeContainsExpression = PredicateSearchUtils.containsExpression(qManufacture.code, manufactureCode);
			predicates.add(manufactureCodeContainsExpression);
		}

		//manufactureBoxtype
		Long manufactureBoxtype = EsafeboxUtils.getFieldValueAsLong(ManufactureListFieldName.MANUFACTURE_BOXTYPE.getEsafeboxFieldName().getFieldName(), data);
		if (manufactureBoxtype != null) {
			BooleanExpression manufactureBoxtypeEqExpression = PredicateSearchUtils.eqExpression(qManufacture.boxtypeId, manufactureBoxtype);
			predicates.add(manufactureBoxtypeEqExpression);
		}

		//factureStatus
		String factureStatus = EsafeboxUtils.getFieldValueAsString(ManufactureListFieldName.FACTURE_STATUS.getEsafeboxFieldName().getFieldName(), data);
		if (!StringUtils.isEmpty(factureStatus)) {
			BooleanExpression factureStatusEqExpression = PredicateSearchUtils.eqExpression(qManufacture.status, factureStatus);
			predicates.add(factureStatusEqExpression);
		}

		//created
		String createdFrom = EsafeboxUtils.getFieldValueAsString(ManufactureListFieldName.FROM_DATE.getEsafeboxFieldName().getFieldName(), data);
		String createdTo = EsafeboxUtils.getFieldValueAsString(ManufactureListFieldName.TO_DATE.getEsafeboxFieldName().getFieldName(), data);
		BooleanExpression createdDateBetweenExpress = PredicateSearchUtils.betweenExpression(qManufacture.created, createdFrom, createdTo);
		predicates.add(createdDateBetweenExpress);
		
		//Manufacture
		BooleanExpression isNotNullManufactureCodeExpression = qManufacture.isNotNull();
		predicates.add(isNotNullManufactureCodeExpression);
				
        return ExpressionUtils.allOf(predicates);
    }

}
