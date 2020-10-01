package com.ecpay.esafebox.controller.model.userbox;

import com.ecpay.entities.common.ResponseMessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveUserBoxCrudResponse extends ResponseMessage {
	private static final long serialVersionUID = 6394810515886877858L;
	private ActiveUserBoxCrudResponseData responseData;

	@Data
	public class ActiveUserBoxCrudResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "userBoxId", required = true)
		private String userBoxId;

		@ApiModelProperty(dataType = "Long", example = "string", notes = "box's serial", required = true)
		private String serialBox;

		@ApiModelProperty(dataType = "String", example = "string", notes = "keyBox")
		private String keyBox;

		@ApiModelProperty(dataType = "Long", example = "string", notes = "boxId", required = true)
		private String boxId;

		@ApiModelProperty(dataType = "String", example = "string", notes = "kpBox")
		private String kpBox;
		
		@ApiModelProperty(dataType = "String", example = "string", notes = "alias")
		private String alias;

		@Override
		public String toString() {
			return "ActiveUserBoxCrudResponseData {\"userBoxId\": \"" + userBoxId + "\", \"serialBox\": \"" + serialBox
					+ "\", \"keyBox\": \"" + keyBox + "\", \"boxId\": \"" + boxId + "\", \"kpBox\": \"" + kpBox + "\"}";
		}

	}
}
