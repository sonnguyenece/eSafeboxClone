package com.ecpay.esafebox.dto.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum EsafeboxErrorCode {

	/**
	 * COMMON
	 */
	SUCCESSFUL("0000", "Successful"),
	SYSTEM_ERROR("0001", "System error"),
	PARAM_IS_MISSED("0996", "Parameter [%s] is missed."),
	SORTS_PARAM_IS_INVALID("0998", "Data of field [%s] is invalid. Its values must be in [%s]"),
	DATA_IS_INVALID_DATA_TYPE("0998", "Data type of field [%s] is invalid. It is required data type is [%s]"),
	DATA_IS_INVALID_LENGTH("0998", "Data for field [%s] is not valid. The length must <= [%s]"),
	DATA_IS_INVALID_LENGTH_2("0998", "Data for field [%s] is not valid. The length must >= [%s]"),
	DATA_IS_INVALID_PATTERN("0998", "Data for field [%s] is not valid. It must match with pattern [%s]"),
	DATA_IS_INVALID_FIXED_VALUES("0998", "Data of field [%s] is invalid. It must in %s. Actual your data is [%s]"),
	DATA_IS_DUPLICATE_IN_LIST("8052", "Data of field [%s] in list is duplicated."),
	UNAUTHORIZED("0006", "Login required"),
	METHOD_ARGUMENT_NOT_VALID("0004", "Bad request %s"),
	MISSING_SERVLET_REQUEST_PARAMETERS("0004", "Miss parameters %s"),
	API_NOT_FOUND("0004", "You called an available API. This API [%s] does not exists in system."),
	CONSTRAINT_VIOLATION("0001", "System error %s"),
	RESOURCE_NOT_FOUND("0005", "Data not found!!!"),
	API_DOES_NOT_SUPPORT("0006", "API [%s] does not support this case. Please contact developer [esafebox module]"),
	DATE_PATTERN_NOT_VALID("6057", "Date value [%s] for field [%s] is not valid pattern. This field required pattern [%s]"),
	NO_SEARCH_DATA_FOUND("S017", "No data found with search criteria"),
	NO_DATA_FOUND("S000", "Data was not found"),
	DATA_MAX_MIN("0998", "Data field [%s] value [%s] is invalid. [(minValue, maxValue) is (%s, %s)]"),
	DATA_IS_INVALID("0998", "Data field [%s] value [%s] is invalid."),
	/**
	 * MANUFACTURE
	 */
	MANUFACTURE_CODE_ALREADY_EXISTS("S001", "Manufacture's Code [%s] existed."),
	MANUFACTURE_ALREADY_EXISTS("S003", "Manufacture [%s] existed."),
	MANUFACTURE_NOT_EXISTS("S004", "Manufacture [%s] not existed."),
	MANUFACTURE_STATUS_C_NOT_ALLOW("S005", "Manufacture's status \"C\" not allowed"),
	MANUFACTURE_STATUS_CHANGE_NOT_ALLOW("S006", "Manufacture's status not allowed to change from \"A\" to \"D\" and vice versa."),
	/**
	 * BOXTYPE
	 */
	BOXTYPE_EXISTS("S002", "BoxType's [%s] existed."),
	BOXTYPE_NOT_EXISTS("S006", "Boxtype [%s] does not exists."),
	BOXTYPE_ID_NOT_EXISTS("S016", "Boxtype's ID [%s] does not exists."),
	BOXTYPE_CODE_EXISTS("S012", "BoxType's Code [%s] existed."),
	BOXTYPE_CODE_NOT_EXISTS("S018", "BoxType's Code [%s] does not exists."),
	BOX_PRICE_SALE_IS_INVALID("0998", "Data field boxPriceSale's value [%s] is invalid. Its value must be less than or equals to boxTypePrice's value [%s]"),
	
	SET_ATTRIBUTE_NOT_EXISTS("S013", "Set [%s] does not exists."),
	SET_ATTRIBUTE_CODE_EXISTS("S019", "Set's code [%s] is existed."),
	SET_NOT_EXISTS("S020", "Set [%s] does not exists."),
	
	ATTRIBUTE_NOT_ALLOW_EMPTY("S014", "attributeId/attributeValue not allow empty."),
	ATTRIBUTE_ID_NOT_EXISTS("S015", "attributeId [%s] not exists."),
	ATTRIBUTE_CODE_EXISTS("S021", "Attribute Code [%s] is existed."),
	ATTRIBUTE_TYPE_NOT_ALLOWED("S022", "Attribute Type [%s] not allowed. Its value must be in DATE or STRING or NUMERIC."),
	ATTRIBUTE_NOT_EXISTS("S023", "Attribute [%s] not exists."),
	
	UOM_TYPE_EXISTS("S024", "UOMType [%s] is existed."),
	UOM_TYPE_NOT_EXISTS("S025", "UOMType [%s] does not exists."),
	UOM_ABREVIATION_EXISTS("S026", "UOM Abbreviation [%s] existed."),
	UOM_ABREVIATION_NOT_EXISTS("S027", "UOM Abbreviation [%s] not exists."),
	
	LIMIT_CODE_EXISTS("S028", "Limit code [%s] is existed."),
	LIMIT_ID_DOES_NOT_EXISTS("S029", "Limit id [%s] does not exists."),
	USER_PAIR_IS_EXISTED("S030", "Setting userTo [%s] for userFrom [%s] is existed."),
	LIMIT_VALUE_ID_DOES_NOT_EXISTS("S032", "Limit value id [%s] does not exists."),
	BOX_TYPE_ATTRIBUTE_DOES_NOT_ALLOW_EMPTY("S033", "box type attribute does not allow empty."),
	
	BOX_DOES_NOT_EXISTS("S108", "Box does not exists."),
	USER_BOX_NOT_EXISTS("S034", "UserBox does not exists."),

	/**
	 * SET
	 */
	SET_CODE_IS_INVALID("0998", "Data field [setCode] value [%s] is invalid. " +
			"Its length must be greater than 2 characters."),
	SET_NAME_IS_INVALID("0998", "Data field [setName] value [%s] is invalid. " +
			"Its length must be greater than 3 characters."),

	/**
	 * ATTRIBUTE
	 */
	ATTRIBUTE_WRONG_DATATYPE("S022", "Data field [attributeType] value [%s] is invalid. " +
			"Only DATE/STRING/NUMERIC is accepted."),
	UOM_NOT_EXISTS("S027", "Uom [%s] does not exists."),
	UOM_ID_NOT_EXISTS("S027", "UomId [%s] does not exists."),
	ATTRIBUTE_CODE_IS_INVALID("0998", "Data field [attributeCode] value [%s] is invalid. " +
			"Its length must be greater than 2 characters."),
	ATTRIBUTE_NAME_IS_INVALID("0998", "Data field [attributeName] value [%s] is invalid. " +
			"Its length must be greater than 3 characters."),
	ATTRIBUTE_NOT_EXIST("S020", "Attribute [%s] not exists."),
	ATTRIBUTE_DATE_FORMAT_IS_INVALID("0998", "Data field [attributeFormat] value [%s] is invalid." +
			"It's format must be in YYYYMMDD or YYYY/MM/DD or YYYY-MM-DD or YYYY:MM:DD "),
	ATTRIBUTE_NUMBERIC_FORMAT_IS_INVALID("0998", "Data field [attributeFormat] value [%s] is invalid."),
	/**
	 * TRANSACTION
	 */
	BOX_SERIAL_NOT_EXISTS("S100", "Box Serial [%s] not exists."),
	INVALID_KEY_BOX_CLIENT("S102", "Submited Box's key invalid."),
	BOX_KEY_NOT_EXISTS("S103", "Box's Key not exists"),
	INVALID_ITEMS("S104", "Invalid items"),
	TRANSACTION_SIGNATURE_EXIST("S105", "Transaction Signature existed"),
	TRANSACTION_SIGNATURE_IVALID("S106", "Transaction Signature invalid"),
	TRANSACTION_EXISTS("S107", "Transaction [%s] existed"),
	BOX_ID_NOT_EXISTS("S108", "BoxId [%s] not exists"),
	ISSUER_CODE_NOT_EXISTS("S109", "IssuerCode [%s] not exists"),
	VALUES_NOT_EXISTS("S110", "Value [%s] not exists"),
	ITEMS_LIST_NOT_EXISTS("S111", "List Items not exists"),
	;

    String errorCode;
    String description;

    public String getDescription(String...args) {
    	return String.format(description, args);
	}

	public String getDescription(List<String> args) {
    	if (CollectionUtils.isEmpty(args)) {
    		return getDescription();
		}

		return getDescription(args.stream().toArray(String[]::new));
	}
	
	public static EsafeboxErrorCode findByCode(String code) {
		EsafeboxErrorCode[] var1 = values();
        int len = var1.length;
        for (int var3 = 0; var3 < len; ++var3) {
        	EsafeboxErrorCode type = var1[var3];
            if (type.getErrorCode().equals(code)) {
                return type;
            }
        }
        return EsafeboxErrorCode.SYSTEM_ERROR;
    }
}
