package com.ecpay.esafebox.controller.model.value;

import com.ecpay.entities.common.ResponseMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBoxTypeValueResponse extends ResponseMessage {
	private static final long serialVersionUID = -892792447565255287L;
	private CreateBoxTypeValueResponseData responseData;

	@Data
	public class CreateBoxTypeValueResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "Boxtype id", required = true)
		private String boxTypeId;

		@ApiModelProperty(dataType = "Object", required = true, example = "[{\"attributeId\":null,\"attributeValue\":\"\"}]", notes = "boxTypeAttributes")
		private Object boxTypeAttributes;

		@Override
		public String toString() {
			return "CreateBoxTypeValueResponseData {\"boxTypeId\": \"" + boxTypeId + "\", \"boxTypeAttributes\": \""
					+ boxTypeAttributes + "\"}";
		}

	}
}
