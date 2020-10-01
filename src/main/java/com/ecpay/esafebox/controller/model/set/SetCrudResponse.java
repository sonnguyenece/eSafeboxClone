package com.ecpay.esafebox.controller.model.set;

import com.ecpay.entities.common.ResponseMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SetCrudResponse extends ResponseMessage {
    private static final long serialVersionUID = 1L;
    private SetCrudResponseData responseData;

    @Data
    private class SetCrudResponseData {

        @ApiModelProperty(dataType = "Long", example = "1", notes = "SetId's id", required = true)
        private String setId;

        @ApiModelProperty(dataType = "String", example = "string", notes = "Set's code", required = true)
        private String setCode;

        @ApiModelProperty(dataType = "String", example = "string", notes = "Set's name", required = true)
        private String setName;

        @Override
        public String toString() {
            return "SetCrudResponseData {\"setId\": \"" + setId + "\", \"setCode\": \"" + setCode
                    + "\", \"setName\": \"" + setName + "\"}";
        }
    }


}
