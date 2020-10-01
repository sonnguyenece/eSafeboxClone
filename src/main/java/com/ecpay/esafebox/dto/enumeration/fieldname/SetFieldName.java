package com.ecpay.esafebox.dto.enumeration.fieldname;

import java.util.Arrays;
import java.util.List;

import com.ecpay.esafebox.dto.EsafeboxFieldName;
import com.ecpay.esafebox.dto.PatternDetail;
import com.ecpay.esafebox.dto.enumeration.EsafeboxDataType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SetFieldName {

    /**
     * SET
     */
    SET_ID(EsafeboxFieldName.builder()
            .fieldName("setId")
            .entityFieldName("id")
            .dataType(EsafeboxDataType.LONG)
            .minValue(0L)
            .maxValue(999999999999999999L)
            .isMandatory(true)
            .build()),

    SET_CODE(EsafeboxFieldName.builder()
            .fieldName("setCode")
            .entityFieldName("code")
            .dataType(EsafeboxDataType.STRING)
            .length(8)
            .pattern(PatternDetail.builder()
                    .pattern("^[A-Za-z0-9]*$")
                    .description("Pattern for code")
                    .errorMessage("Data for field [code] is not valid. It contains the special characters or accented Vietnamese.")
                    .build())
            .isMandatory(true)
            .build()),

    SET_NAME(EsafeboxFieldName.builder()
            .fieldName("setName")
            .entityFieldName("name")
            .dataType(EsafeboxDataType.STRING)
            .length(128)
            .pattern(PatternDetail.builder()
                    .pattern("^[^`~!@#$%^&*()_+={}\\[\\]|\\\\:;“’<,>.?๐฿]*$")
                    .description("Pattern for code")
                    .errorMessage("Data for field [name] is not valid. It contains the special character(s).")
                    .build())
            .isMandatory(false)
            .build()),

    SET_NAME_CREATE_UPDATE(EsafeboxFieldName.builder()
            .fieldName("setName")
            .entityFieldName("name")
            .dataType(EsafeboxDataType.STRING)
            .length(128)
            .pattern(PatternDetail.builder()
                    .pattern("^[^`~!@#$%^&*()_+={}\\[\\]|\\\\:;“’<,>.?๐฿]*$")
                    .description("Pattern for code")
                    .errorMessage("Data for field [name] is not valid. It contains the special character(s).")
                    .build())
            .isMandatory(true)
            .build()),;

    EsafeboxFieldName esafeboxFieldName;

    public static List<EsafeboxFieldName> getSearchFieldNames() {
        return Arrays.asList(
                SET_ID.getEsafeboxFieldName(),
                SET_CODE.getEsafeboxFieldName(),
                SET_NAME.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_NUMBER.getEsafeboxFieldName(),
                PaginationSortingFieldName.PAGE_SIZE.getEsafeboxFieldName(),
                PaginationSortingFieldName.SORTS.getEsafeboxFieldName(),
                PaginationSortingFieldName.DIRECTION.getEsafeboxFieldName()
        );
    }

    public static List<EsafeboxFieldName> getListSetSortFieldNames() {
        return Arrays.asList(
                SET_ID.getEsafeboxFieldName(),
                SET_CODE.getEsafeboxFieldName(),
                SET_NAME.getEsafeboxFieldName()
        );
    }
}
