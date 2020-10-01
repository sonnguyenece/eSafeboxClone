package com.ecpay.esafebox.controller.model.box;

import com.ecpay.entities.common.ResponseMessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoxCrudResponse extends ResponseMessage {
	private static final long serialVersionUID = -7407306459468189538L;
	private BoxCrudResponseData responseData;

	@Data
	public class BoxCrudResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "manuafacture's id", required = true)
		private String manuafactureId;

		@ApiModelProperty(dataType = "Long", example = "1", notes = "boxType's id", required = true)
		private String boxTypeId;

		@ApiModelProperty(dataType = "String", example = "MEUCIQCGQ", notes = "manuafacture's code", required = true)
		private String manuafactureCode;

		@ApiModelProperty(dataType = "String", example = "P", notes = "facture's status", required = true)
		private String factureStatus;

		@Override
		public String toString() {
			return "BoxCrudResponseData {\"manuafactureId\": \"" + manuafactureId + "\", \"boxTypeId\": \"" + boxTypeId
					+ "\", \"manuafactureCode\": \"" + manuafactureCode + "\", \"factureStatus\": \"" + factureStatus
					+ "\"}";
		}

	}
}
