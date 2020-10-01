package com.ecpay.esafebox.dto.enumeration.fieldname;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.PatternDetail;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AttributeFieldName {
    ATTRIBUTE_ID(EsafeboxFieldName.builder()
            .fieldName("attributeId")
            .entityFieldName("id")
            .dataType(EsafeboxDataType.LONG)
            .minValue(0L)
            .maxValue(999999999999999999L)
            .isMandatory(true)
            .build()),
    ATTRIBUTE_CODE(EsafeboxFieldName.builder()
            .fieldName("attributeCode")
            .entityFieldName("code")
            .dataType(EsafeboxDataType.STRING)
            .length(8)
            .pattern(PatternDetail.builder()
                    .pattern("^[A-Za-z0-9]*$")
                    .description("Pattern for attributeCode")
                    .errorMessage("Data for field [code] is not valid. It contains the special characters or accented Vietnamese.")
                    .build())
            .isMandatory(true)
            .build()),
    ATTRIBUTE_NAME(EsafeboxFieldName.builder()
            .fieldName("attributeName")
            .entityFieldName("name")
            .dataType(EsafeboxDataType.STRING)
            .length(128)
            .pattern(PatternDetail.builder()
                    .pattern("^[^`~!@#$%^&*()_+={}\\[\\]|\\\\:;“’<,>.?๐฿]*$")
                    .description("Pattern for attributeName")
                    .errorMessage("Data for field [name] is not valid. It contains the special character(s).")
                    .build())
            .isMandatory(true)
            .build()),
    ATTRIBUTE_FORMAT(EsafeboxFieldName.builder()
            .fieldName("attributeFormat")
            .entityFieldName("format")
            .dataType(EsafeboxDataType.STRING)
            .length(24)
            .isMandatory(true)
            .build()),
    ATTRIBUTE_TYPE(EsafeboxFieldName.builder()
            .fieldName("attributeType")
            .entityFieldName("type")
            .dataType(EsafeboxDataType.STRING)
            .length(128)
            .pattern(PatternDetail.builder()
                    .pattern("^[A-Za-z0-9]*$")
                    .description("Pattern for attributeCode")
                    .errorMessage("Data for field [code] is not valid. It contains the special characters or accented Vietnamese.")
                    .build())
            .isMandatory(true)
            .build()),
    ATTRIBUTE_UOM(EsafeboxFieldName.builder()
            .fieldName("uomId")
            .entityFieldName("uomId")
            .dataType(EsafeboxDataType.LONG)
            .minValue(0L)
            .maxValue(999999999999999999L)
            .isMandatory(true)
            .build()),
    ATTRIBUTE_UOM_UPDATE(EsafeboxFieldName.builder()
            .fieldName("uomId")
            .entityFieldName("id")
            .dataType(EsafeboxDataType.LONG)
            .minValue(0L)
            .maxValue(999999999999999999L)
            .isMandatory(true)
            .build()),
    ATTRIBUTE_SET(EsafeboxFieldName.builder()
            .fieldName("setId")
            .entityFieldName("setId")
            .dataType(EsafeboxDataType.LONG)
            .minValue(0L)
            .maxValue(999999999999999999L)
            .isMandatory(true)
            .build()),
    ATTRIBUTE_SET_UPDATE(EsafeboxFieldName.builder()
            .fieldName("setId")
            .entityFieldName("setId")
            .dataType(EsafeboxDataType.LONG)
            .minValue(0L)
            .maxValue(999999999999999999L)
            .isMandatory(false)
            .build()),
    ;
    EsafeboxFieldName esafeboxFieldName;


    public static List<EsafeboxFieldName> getSearchFieldNames() {
        return Arrays.asList(
                ATTRIBUTE_ID.getEsafeboxFieldName(),
                ATTRIBUTE_CODE.getEsafeboxFieldName(),
                ATTRIBUTE_NAME.getEsafeboxFieldName(),
                ATTRIBUTE_TYPE.getEsafeboxFieldName(),
                ATTRIBUTE_FORMAT.getEsafeboxFieldName(),
                ATTRIBUTE_SET.getEsafeboxFieldName(),
                ATTRIBUTE_UOM.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
                PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
                PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
        );
    }

    public static List<EsafeboxFieldName> getListSetSortFieldNames() {
        return Arrays.asList(
                ATTRIBUTE_ID.getEsafeboxFieldName(),
                ATTRIBUTE_CODE.getEsafeboxFieldName(),
                ATTRIBUTE_NAME.getEsafeboxFieldName(),
                ATTRIBUTE_UOM.getEsafeboxFieldName()
        );
    }

    public void setEsafeboxFieldName(EsafeboxFieldName esafeboxFieldName) {
        this.esafeboxFieldName = esafeboxFieldName;
    }
}
