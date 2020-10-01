package com.ecpay.esafebox.controller.model.userpair;

import com.ecpay.entities.common.ResponseMessage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPairCrudResponse extends ResponseMessage {
	private static final long serialVersionUID = -1563842238549218773L;
	
	private UserPairCrudResponseData userToList;

	@Data
	public class UserPairCrudResponseData {
		@ApiModelProperty(dataType = "Long", example = "1", notes = "userpair id", required = true)
		private String userPairId;

		@ApiModelProperty(dataType = "Long", example = "2", notes = "user to", required = true)
		private String userTo;

		@ApiModelProperty(dataType = "String", example = "N", notes = "userpair's status", required = true)
		private String userPairStatus;

		@Override
		public String toString() {
			return "UserPairCrudResponseData {\"userPairId\": \"" + userPairId + "\", \"userTo\": \"" + userTo
					+ "\", \"userPairStatus\": \"" + userPairStatus + "\"}";
		}

	}
}
