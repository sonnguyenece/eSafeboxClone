package com.ecpay.esafebox.controller.model.boxtype;



import java.util.Set;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.dto.BoxtypeValueDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxTypeInquiryResponse extends ResponseMessage{
	private static final long serialVersionUID = 1L;
	private BoxTypeInquiryResponseData responseData;
	
	@Data
	public class BoxTypeInquiryResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "Boxtype's id", required = true)
		private String boxTypeId;
		
		@ApiModelProperty(dataType = "String", example = "string", notes = "Boxtype's code", required = true)
		private String boxTypeCode;
		
		@ApiModelProperty(dataType = "String", example = "string", notes = "Boxtype's name", required = true)
		private String boxTypeName;
		
		@ApiModelProperty(dataType = "Long", example = "25000000", notes = "Boxtype's price", required = true)
		private String boxTypePrice;
		
		@ApiModelProperty(dataType = "Long", example = "2500000", notes = "Boxtype's price sale")
		private String boxTypeSale;
		
		@ApiModelProperty(dataType = "Long", example = "1", notes = "Set Id", required = true)
		private String setId;
		
		@ApiModelProperty(dataType = "String", example = "Base64String", notes = "Boxtype's icon (Base64String)")
		private String boxTypeIcon;

		@ApiModelProperty(dataType = "Array", example = "[{\"valueId\": \"1\", \"attributeId\": \"1\", \"attributeValue\": \"10\"}]", notes = "Boxtype's limits")
		private Set<BoxtypeValueDto> boxTypeAttributes;

		@Override
		public String toString() {
			return "BoxTypeCrudResponseData {\"boxTypeId\": \"" + boxTypeId + "\", \"boxTypeCode\": \"" + boxTypeCode
					+ "\", \"boxTypeName\": \"" + boxTypeName + "\", \"boxTypePrice\": \"" + boxTypePrice
					+ "\", \"boxTypeSale\": \"" + boxTypeSale + "\", \"setId\": \"" + setId + "\"}";
		}
		
	}
	
}
