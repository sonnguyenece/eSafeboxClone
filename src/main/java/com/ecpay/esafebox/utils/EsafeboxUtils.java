package com.ecpay.esafebox.utils;

import com.ecpay.entities.common.ResponseMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecpay.esafebox.algorithm.EllipticCurve;
import com.ecpay.esafebox.dto.enumeration.EsafeboxErrorCode;
import com.ecpay.esafebox.exception.ESafeboxException;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.ecpay.entities.common.EcConstants.*;

@Slf4j
@UtilityClass
public class EsafeboxUtils {
	/**
	 * Parse json string to map
	 * @param map
	 * @return a json string of response message.
	 */
	public String convertMap2JsonString(Map<String, Object> map) {
		try {
			return MAPPER.writeValueAsString(map);
		} catch (Exception e) {
			log.warn("Can not get json string from {}. Reason [{}]", map, e.getLocalizedMessage());
			return null;
		}
	}

    /**
     * Parse json string to map
     * @param jsonString is a string with json format
     * @return a map with key and value Or null if any exception occurs.
     */
    public Map<String, Object> convertJsonStringToMap(String jsonString) {
        try {
            JacksonJsonParser jacksonJsonParser = new JacksonJsonParser(new ObjectMapper());
            return jacksonJsonParser.parseMap(jsonString);
        } catch (Exception e) {
            log.warn("Can not parse json {} to MAP object. Reason [{}]", jsonString, e.getLocalizedMessage());
            return null;
        }
    }

	/**
	 * Parse object to map
	 * @param obj is a #Object
	 * @return a map with key and value Or null if any exception occurs.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> convertObject2Map(Object obj) {
		if (obj == null) {
			return null;
		}

		try {
			return MAPPER.convertValue(obj, Map.class);
		} catch (IllegalArgumentException e) {
			log.warn("Can not convert data {} to MAP object. Reason [{}]", obj, e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * Convert #ResponseMessage to byte
	 * @param responseMessage is a #ResponseMessage
	 * @return bytes
	 */
	public byte[] convertResponseMessage2Bytes(ResponseMessage responseMessage) {
		if (responseMessage == null) {
			return null;
		}

		try {
			String dataString = MAPPER.writeValueAsString(responseMessage);
			return dataString.getBytes();
		} catch (JsonProcessingException e) {
			log.warn("Can not convert {} to bytes. Reason: {}", responseMessage, e.getLocalizedMessage());
			return null;
		}
	}

    /**
     * Check a response is success OR failure
     * @param response is a map which contains the field [responseCode]
     * @return
     * - true if [responseCode] is equals 000
     * - false if [responseCode] is not equals 000
     */
    public boolean isSuccessResponse(Map<String, Object> response) {
        return response != null
                && response.get(RESPONSE_CODE_FIELD) != null
                &&  response.get(RESPONSE_CODE_FIELD).toString().equals(RESPONSE_CODE_SUCCESS_VALUE);
    }

	/**
	 * Check a response is success OR failure
	 * @param responseMessage see #ResponseMessage
	 * @return
	 * - true if [responseCode] is equals 000
	 * - false if [responseCode] is not equals 000
	 */
	public boolean isSuccessResponse(ResponseMessage responseMessage) {
		return responseMessage != null
				&& responseMessage.getResponseCode() != null
				&&  responseMessage.getResponseCode().equals(RESPONSE_CODE_SUCCESS_VALUE);
	}

    /**
     * Build a standard response from code, message, data
     * @param responseCode is a String
     * @param responseMessage is a String
     * @param responseData is a object
     * @return a map
     */
    public ResponseMessage buildStandardResponse(String responseCode, String responseMessage, Object responseData) {
        return ResponseMessage.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .responseData(responseData)
                .build();
    }

	/**
	 * Build a standard response from code, message, data
	 * @param map
	 * @return #ResponseMessage
	 */
	public ResponseMessage buildStandardResponse(Map<String, Object> map) {
		if (map == null) {
			return ResponseMessage.builder().build();
		}

		return ResponseMessage.builder()
				.responseCode(map.get(RESPONSE_CODE_FIELD) == null ? null : (String) map.get(RESPONSE_CODE_FIELD))
				.responseMessage(map.get(RESPONSE_MESSAGE_FIELD) == null ? null : (String) map.get(RESPONSE_MESSAGE_FIELD))
				.responseData(map.get(RESPONSE_DATA_FIELD))
				.build();
	}

    /**
     * Build a standard response from #ResponseMessage
     * @param responseMessage see #ResponseMessage
     * @return a map
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> buildStandardResponse(ResponseMessage responseMessage) {
        return MAPPER.convertValue(responseMessage, Map.class);
    }

	/**
	 * Build a standard response from #ResponseMessage
	 * @param notificationErrorCode see #NotificationErrorCode
	 * @param args is an array string value to build the message
	 * @return a map
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> buildMapStandardErrorResponse(EsafeboxErrorCode esafeboxErrorCode, List<String> args) {
		ResponseMessage responseMessage = ResponseMessage.builder()
				.responseCode(esafeboxErrorCode.getErrorCode())
				.responseMessage(esafeboxErrorCode.getDescription(args))
				.build();

		return MAPPER.convertValue(responseMessage, Map.class);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> buildMapStandardErrorResponse(EsafeboxErrorCode esafeboxErrorCode, List<String> args, Long responseTime) {
		ResponseMessage responseMessage = ResponseMessage.builder()
				.responseCode(esafeboxErrorCode.getErrorCode())
				.responseMessage(esafeboxErrorCode.getDescription(args))
				.responseTime(responseTime)
				.build();

		return MAPPER.convertValue(responseMessage, Map.class);
	}

	/**
	 * Build a standard response from #ResponseMessage
	 * @param notificationErrorCode see #NotificationErrorCode
	 * @param args is an array string value to build the message
	 * @return a map
	 */
	public ResponseMessage buildObjectStandardErrorResponse(EsafeboxErrorCode esafeboxErrorCode, List<String> args) {
		return ResponseMessage.builder()
				.responseCode(esafeboxErrorCode.getErrorCode())
				.responseMessage(esafeboxErrorCode.getDescription(args))
				.build();
	}

	public ResponseMessage buildObjectStandardErrorResponse(EsafeboxErrorCode esafeboxErrorCode) {
		return buildObjectStandardErrorResponse(esafeboxErrorCode, null);
	}

	/**
	 * Get array value of fieldName in map
	 * @param fieldName as a key of map
	 * @param dataMap a map
	 * @return value as an array of fieldName in map
	 */
	@SuppressWarnings("unchecked")
	public List<String> getFieldValueAsArrayOfString(String fieldName, Map<String, Object> dataMap) {
		Object object = dataMap.get(fieldName);
		if (object instanceof List) {
			return (List<String>) object;
		} else {
			log.warn("Can not parse value of field [{}] to list of String", fieldName);
			return null;
		}
	}
	
	/**
	 * Get array value of fieldName in map
	 * @param fieldName as a key of map
	 * @param dataMap a map
	 * @return value as an array of fieldName in map
	 */
	public List<Long> getFieldValueAsArrayOfLong(String fieldName, Map<String, Object> dataMap) {
		try {
			String fieldDataAsString = getFieldValueAsString(fieldName, dataMap);
			if (StringUtils.isEmpty(fieldDataAsString)) {
				return null;
			}

			return Constants.MAPPER.readValue(fieldDataAsString, new TypeReference<List<Long>>() {});
		} catch (NumberFormatException | IOException e) {
			log.error("Can not parse value of field [{}] to Long. Reason: {}", fieldName, e.getLocalizedMessage());
			return null;
		}
	}

    /**
     * Get string value of fieldName in map
     * @param fieldName as a key of map
     * @param dataMap a map
     * @return value as a string of fieldName in map
     */
    public String getFieldValueAsString(String fieldName, Map<String, Object> dataMap) {
    	Object result = getFieldValue(fieldName, dataMap);
        return result == null ? null : result.toString();//.trim();
    }

    /**
     * Get long value of fieldName in map
     * @param fieldName as a key of map
     * @param dataMap a map
     * @return value as a long of fieldName in map
     */
    public Long getFieldValueAsLong(String fieldName, Map<String, Object> dataMap) {
        try {
            return Long.parseLong(getFieldValueAsString(fieldName, dataMap));
        } catch (NumberFormatException nfe) {
            log.warn("Can not parse value to Long: {}", nfe.getLocalizedMessage());
            return null;
        }
    }

	/**
	 * Get int value of fieldName in map
	 * @param fieldName as a key of map
	 * @param dataMap a map
	 * @return value as a int of fieldName in map
	 */
	public Integer getFieldValueAsInteger(String fieldName, Map<String, Object> dataMap) {
		try {
			return Integer.parseInt(getFieldValueAsString(fieldName, dataMap));
		} catch (NumberFormatException nfe) {
			log.warn("Can not parse value to Integer: {}", nfe.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * Get string value of fieldName in map
	 * @param value as a object
	 * @return long value or null
	 */
	public Long getValueAsLong(Object value) {
		if (value == null) {
			return null;
		}

		try {
			return Long.parseLong(value.toString());
		} catch (NumberFormatException nfe) {
			log.warn("Can not parse value {} to Long: {}", value, nfe.getLocalizedMessage());
			return null;
		}
	}

    /**
     * Get map object value of fieldName in map
     * @param fieldName as a key of map
     * @param dataMap a map
     * @return value as a map object of fieldName in map
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> getFieldValueAsMap(String fieldName, Map<String, Object> dataMap) {
        return (Map<String, Object>) getFieldValue(fieldName, dataMap);
    }

    public boolean hasText(Object object) {
    	if (object == null || !(object instanceof String )) {
    		return false;
		}

    	String objectString = object.toString();
    	if (!StringUtils.isEmpty(objectString) || StringUtils.hasText(objectString)) {
    		return true;
		}

		return false;
	}

	/**
	 * Get array value of fieldName in map
	 * @param fieldName as a key of map
	 * @param dataMap a map
	 * @return value as an array of fieldName in map
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getFieldValueAsArrayOfObject(String fieldName, Map<String, Object> dataMap) {
		Object object = dataMap.get(fieldName);
		if (object == null || !(object instanceof List)) {
			log.warn("Can not get list object for fieldName [{}]. Reason: data is NULL or is not instance of List", fieldName);
			return null;
		}

		return (List<Object>) object;
	}

    /**
     * Get value of fieldName in map
     * @param fieldName as a key of map
     * @param dataMap a map
     * @return value of fieldName in map
     */
    private Object getFieldValue(String fieldName, Map<String, Object> dataMap) {
        if (StringUtils.isEmpty(fieldName) || dataMap == null || dataMap.isEmpty()) {
            return null;
        }

        return dataMap.get(fieldName);
    }

	public LocalDateTime convertString2LocalDateTime(Object dateObject) {
		if (dateObject == null || org.springframework.util.StringUtils.isEmpty(dateObject.toString())) {
			return null;
		}

		try {
			return LocalDateTime.parse(dateObject.toString(), DATE_TIME_WITHOUT_MILISECOND_FORMATTER);
		} catch (DateTimeParseException dtpe) {
			log.warn("Can not parse {} to LocalDateTime via pattern {}. Reason {}", dateObject.toString(), DATE_TIME_WITHOUT_MILISECOND_FORMATTER, dtpe.getLocalizedMessage());
			return null;
		}
	}

	public LocalDate convertString2LocalDate(Object dateObject) {
		if (dateObject == null || org.springframework.util.StringUtils.isEmpty(dateObject.toString())) {
			return null;
		}

		try {
			return LocalDate.parse(dateObject.toString(), DATE_FORMATTER);
		} catch (DateTimeParseException dtpe) {
			log.warn("Can not parse {} to LocalDate via pattern {}. Reason {}", dateObject.toString(), DATE_FORMATTER, dtpe.getLocalizedMessage());
			return null;
		}
	}

	public static <T> List<T> getListFromIterator(Iterator<T> iterator) 
    { 
  
        // Create an empty list 
        List<T> list = new ArrayList<>(); 
  
        // Add each element of iterator to the List 
        iterator.forEachRemaining(list::add); 
  
        // Return the List 
        return list; 
    } 
	
	
	public static String toUnsignedVietnamese(String signed) {
		
		if(Objects.isNull(signed) || signed.trim().equals(""))
			return "";
		
		signed = signed.trim().replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
			.replaceAll("[đ]", "d")
			.replaceAll("[ỳýỵỷỹ]","y")
			.replaceAll("[ùúụủũưừứựửữ]","u")
			.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]","o")
			.replaceAll("[èéẹẻẽêềếệểễ]", "e")
			.replaceAll("[ìíịỉĩ]","i")
			.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]","A")
			.replaceAll("[Đ]","D")
			.replaceAll("[ỲÝỴỶỸ]","Y")
			.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]","U")
			.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]","O")
			.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]","E")
			.replaceAll("[ÌÍỊỈĨ]","I");
			return signed;
	}
	
	/**
	 * 
	 * @param keyBoxClient: base64String, example: MEQCIDreuMTAPAa8R64fj3nZLrWwXv5ccRvBOtVytFXYvOapAiAIaxrbgXp51wS5vjvu2NFazdm3PbMxmopzYyGI+7PFdA==
	 * @param boxKp: Hexadecimal String, example: 0427FB4B2EEE6043351784E95FA9E6FD96983C0C926EC682307B4EBF1B606E56001FD4C52AD0035E8F77CDD13E108517294D9D47EDFBE458F62F5006CB5BFB6234
	 * @param boxKey: Plain-text, example: FB6B333441404AFBBCDFB2B36336313E
	 * @param algorithm, example: EC-secp256k1
	 * @return true: Passed, false: Failed
	 * @throws ESafeboxException
	 */
	public static boolean verifyKeyBoxClient(String boxKp, String algorithm, String boxKey, String keyBoxClient) throws ESafeboxException {
		byte[] signature = Base64.getDecoder().decode(keyBoxClient);
		try (ASN1InputStream asn1 = new ASN1InputStream(signature)) {
			EllipticCurve ec = EllipticCurve.getByCurveName(algorithm).getEllipticCurve();
			ECDSASigner signer = new ECDSASigner();
			signer.init(false, ec.getPublicKeyParameters(Hex.decodeHex(boxKp)));
			DLSequence seq = (DLSequence) asn1.readObject();
			BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
			BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
			return signer.verifySignature(boxKey.getBytes(), r, s);
		} catch (Exception e) {
			log.error("[verifyKeyBoxClient] Exception: " + e.getMessage(), e);
			throw new ESafeboxException(EsafeboxErrorCode.INVALID_KEY_BOX_CLIENT.getErrorCode(),
					EsafeboxErrorCode.INVALID_KEY_BOX_CLIENT.getDescription());
		}
	}
	
	/**
	 * @apiNote: verify item, using two public keys received from ECGW
	 * @param accKp: Key public phat hanh (Base64), example:
	 * @param treKp: Key public luu hanh (Base64), example:
	 * @param algorithm, lay tu tb_user_box, example: EC-secp256k1
	 * @param dataSign: du lieu goc, example:
	 * @param accSign: Signature phat hanh (Base64), example:
	 * @param treSign: Signature luu hanh (Base64), example:
	 * @return true: Passed, false: Failed
	 * @throws ESafeboxException
	 */
	public static boolean verifyAccSignAndTreSign(String accKp, String treKp, String algorithm, String dataSign,
			String accSign, String treSign) throws ESafeboxException {
		log.info("[verifyAccSignAndTreSign] Verify ACC_SIGN...");
		byte[] dataSignByte = dataSign.getBytes();
		try {
			EllipticCurve ec = EllipticCurve.getByCurveName(algorithm).getEllipticCurve();
			byte[] sAccsign = Base64.getDecoder().decode(accSign);
			ECPublicKeyParameters kpAcc = ec.getPublicKeyParameters(Base64.getDecoder().decode(accKp));
			boolean retVal = ec.verify(dataSignByte, sAccsign, kpAcc);
			if (!retVal) {
				log.warn("[verifyAccSignAndTreSign] ACC_SIGN is not valid.");
				return false;
			}
			log.info("[verifyAccSignAndTreSign] ACC_SIGN is valid.");

			log.info("[verifyAccSignAndTreSign] Verify TREE_SIGN...");
			byte[] sTresign = Base64.getDecoder().decode(treSign);
			ECPublicKeyParameters kpTre = ec.getPublicKeyParameters(Base64.getDecoder().decode(treKp));
			retVal = ec.verify(dataSignByte, sTresign, kpTre);
			if (!retVal) {
				log.warn("[verifyAccSignAndTreSign] TREE_SIGN is not valid.");
				return false;
			}
			log.info("[verifyAccSignAndTreSign] TREE_SIGN is valid.");

			return retVal;
		} catch (Exception e) {
			log.error("[verifyAccSignAndTreSign] Exception: " + e.getMessage(), e);
			throw new ESafeboxException(EsafeboxErrorCode.SYSTEM_ERROR.getErrorCode(),
					EsafeboxErrorCode.SYSTEM_ERROR.getDescription());
		}
	}
	
	/**
	 * @apiNote: verify transaction signature received from ECGW in base64 format by box public key (hexadecimal format) in Database
	 * @param transactionSignature: base64String, example: MEQCIDreuMTAPAa8R64fj3nZLrWwXv5ccRvBOtVytFXYvOapAiAIaxrbgXp51wS5vjvu2NFazdm3PbMxmopzYyGI+7PFdA==
	 * @param boxKp: Hexadecimal String, example: 0427FB4B2EEE6043351784E95FA9E6FD96983C0C926EC682307B4EBF1B606E56001FD4C52AD0035E8F77CDD13E108517294D9D47EDFBE458F62F5006CB5BFB6234
	 * @param dataSign: Plain-text, example:
	 * @param algorithm, example: EC-secp256k1
	 * @return true: Passed, false: Failed
	 * @throws ESafeboxException
	 */
	public static boolean verifyTransactionSignature(String boxKp, String algorithm, String dataSign, String transactionSignature) throws ESafeboxException {
		log.info("[verifyAccSignAndTreSign] Verify ACC_SIGN...");
		byte[] dataSignByte = dataSign.getBytes();
		byte[] signature = Base64.getDecoder().decode(transactionSignature);
		try (ASN1InputStream asn1 = new ASN1InputStream(signature)) {
			EllipticCurve ec = EllipticCurve.getByCurveName(algorithm).getEllipticCurve();
			ECDSASigner signer = new ECDSASigner();
			signer.init(false, ec.getPublicKeyParameters(Hex.decodeHex(boxKp)));
			DLSequence seq = (DLSequence) asn1.readObject();
			BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
			BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
			return signer.verifySignature(dataSignByte, r, s);
		} catch (Exception e) {
			log.error("[verifyTransactionSignature] Exception: " + e.getMessage(), e);
			throw new ESafeboxException(EsafeboxErrorCode.TRANSACTION_SIGNATURE_IVALID.getErrorCode(),
					EsafeboxErrorCode.TRANSACTION_SIGNATURE_IVALID.getDescription());
		}
	}
	
	public static void main(String[] args) throws Exception{
		String keyBoxClient = "MEQCIDreuMTAPAa8R64fj3nZLrWwXv5ccRvBOtVytFXYvOapAiAIaxrbgXp51wS5vjvu2NFazdm3PbMxmopzYyGI+7PFdA==";
		String boxKp = "0427FB4B2EEE6043351784E95FA9E6FD96983C0C926EC682307B4EBF1B606E56001FD4C52AD0035E8F77CDD13E108517294D9D47EDFBE458F62F5006CB5BFB6234";
		String boxKey = "FB6B333441404AFBBCDFB2B36336313E";
		String algorithm = "EC-secp256k1";
		System.out.println(verifyKeyBoxClient(boxKp, algorithm, boxKey, keyBoxClient));
		
	}
	
}
