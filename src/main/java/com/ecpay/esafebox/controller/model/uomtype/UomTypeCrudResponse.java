package com.ecpay.esafebox.controller.model.uomtype;

import com.ecpay.entities.common.ResponseMessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UomTypeCrudResponse extends ResponseMessage {
	private static final long serialVersionUID = 6394810515886877858L;
	private UomTypeCrudResponseData responseData;

	@Data
	public class UomTypeCrudResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "Uomtype's id", required = true)
		private String uomTypeId;

		@ApiModelProperty(dataType = "String", example = "string", notes = "Uomtype's code", required = true)
		private String uomTypeCode;

		@ApiModelProperty(dataType = "String", example = "string", notes = "Uomtype's name", required = true)
		private String uomTypeName;

		@Override
		public String toString() {
			return "UomTypeCrudResponseData {\"uomTypeId\": \"" + uomTypeId + "\", \"uomTypeCode\": \"" + uomTypeCode
					+ "\", \"uomTypeName\": \"" + uomTypeName + "\"}";
		}

	}
}
