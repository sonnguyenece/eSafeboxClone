package com.ecpay.esafebox.controller.model.attribute;

import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.PagingResponse;
import com.ecpay.esafebox.dto.AttributeDto;
import com.ecpay.esafebox.dto.SetDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@ApiModel
public class AttributeListResponse extends ResponseMessage {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(dataType = "String", required = true, example ="0000")
    private String responseCode;
    @ApiModelProperty(dataType = "String", required = true, example ="Successful")
    private String responseMessage;
    @ApiModelProperty(dataType = "Object", required = true, example ="Response data")
    private ListAttributeResponseData responseData;
    @ApiModelProperty(dataType = "Long", required = true, example ="Processed time")
    private Long responseTime;

}

@Getter
@Setter
class ListAttributeResponseData extends PagingResponse {
    private Set<AttributeDto> attributeList;
}
