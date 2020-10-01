package com.ecpay.esafebox.controller.model.limit;

import com.ecpay.entities.common.ResponseMessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxTypeLimitCrudResponse extends ResponseMessage {
	private static final long serialVersionUID = -3132415998723959321L;
	private BoxTypeLimitCrudResponseData responseData;

	@Data
	public class BoxTypeLimitCrudResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "limit id", required = true)
		private String limitId;

		@ApiModelProperty(dataType = "String", example = "DPS_SD", notes = "limit code", required = true)
		private String limitCode;

		@ApiModelProperty(dataType = "String", example = "HẠN MỨC GỬI", notes = "limit name", required = true)
		private String limitName;

		@ApiModelProperty(dataType = "Long", example = "1", notes = "limit type", required = true)
		private String limitType;

		@ApiModelProperty(dataType = "Long", example = "1", notes = "box type id", required = true)
		private String boxTypeId;

		@ApiModelProperty(dataType = "Long", example = "1", notes = "limit value id", required = true)
		private String limitValueId;

		@ApiModelProperty(dataType = "Long", example = "1", notes = "limit value", required = true)
		private String limitValue;

		@ApiModelProperty(dataType = "Long", example = "1", notes = "attribute id", required = true)
		private String attributeId;

		@Override
		public String toString() {
			return "BoxTypeLimitCrudResponseData {\"limitId\": \"" + limitId + "\", \"limitCode\": \"" + limitCode
					+ "\", \"limitName\": \"" + limitName + "\", \"limitType\": \"" + limitType
					+ "\", \"boxTypeId\": \"" + boxTypeId + "\", \"limitValueId\": \"" + limitValueId
					+ "\", \"limitValue\": \"" + limitValue + "\", \"attributeId\": \"" + attributeId + "\"}";
		}

	}
}
