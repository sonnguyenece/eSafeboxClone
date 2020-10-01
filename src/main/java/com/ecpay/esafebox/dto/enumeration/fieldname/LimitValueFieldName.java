package com.ecpay.esafebox.dto.enumeration.fieldname;

import java.util.Arrays;
import java.util.List;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum LimitValueFieldName {

    LIMIT_VALUE_ID(EsafeboxFieldName.builder().fieldName("limitValueId").entityFieldName("id")
            .dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999999L).isMandatory(true).build()),
    LIMIT_ID(EsafeboxFieldName.builder().fieldName("limitId").entityFieldName("limitId").dataType(EsafeboxDataType.LONG)
            .minValue(0L).maxValue(999999999999999999L).isMandatory(true).build()),
    LIMIT_VALUE(EsafeboxFieldName.builder().fieldName("limitValue").entityFieldName("value")
            .dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999L).isMandatory(true).build()),
    ATTRIBUTE_ID(EsafeboxFieldName.builder().fieldName("attributeId").entityFieldName("attributeId")
            .dataType(EsafeboxDataType.LONG).minValue(0L).maxValue(999999999999999999L).isMandatory(true).build());

    EsafeboxFieldName esafeboxFieldName;

    public static List<EsafeboxFieldName> getSearchFieldNames() {
        return Arrays.asList(LIMIT_VALUE_ID.getEsafeboxFieldName(), LIMIT_ID.getEsafeboxFieldName(),
                LIMIT_VALUE.getEsafeboxFieldName(), ATTRIBUTE_ID.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
                PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
                PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName());
    }

    public static List<EsafeboxFieldName> getListUomSortFieldNames() {
        return Arrays.asList(LIMIT_VALUE_ID.getEsafeboxFieldName(), LIMIT_ID.getEsafeboxFieldName(),
                LIMIT_VALUE.getEsafeboxFieldName(), ATTRIBUTE_ID.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
                PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
                PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName());
    }
}
