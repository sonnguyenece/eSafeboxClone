package com.ecpay.esafebox.controller.model.boxtype;

import com.ecpay.entities.common.ResponseMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxTypeCrudResponse extends ResponseMessage{
	private static final long serialVersionUID = 1L;
	private BoxTypeCrudResponseData responseData;

	@Data
	public class BoxTypeCrudResponseData {
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

		@Override
		public String toString() {
			return "BoxTypeCrudResponseData {\"boxTypeId\": \"" + boxTypeId + "\", \"boxTypeCode\": \"" + boxTypeCode
					+ "\", \"boxTypeName\": \"" + boxTypeName + "\", \"boxTypePrice\": \"" + boxTypePrice
					+ "\", \"boxTypeSale\": \"" + boxTypeSale + "\", \"setId\": \"" + setId + "\"}";
		}

	}
}
