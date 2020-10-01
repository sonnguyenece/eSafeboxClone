package com.ecpay.esafebox.controller.model.limit;
import com.ecpay.entities.common.ResponseMessage;
import com.ecpay.esafebox.controller.model.PagingResponse;
import com.ecpay.esafebox.dto.LimitDto;
import com.ecpay.esafebox.dto.LimitRemoveReponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@ApiModel
public class BoxTypeLimitRemoveReponse extends ResponseMessage {
    private static final long serialVersionUID = 6772351667630969525L;
    @ApiModelProperty(dataType = "String", required = true, example = "0000")
    private String responseCode;
    @ApiModelProperty(dataType = "String", required = true, example = "Successful")
    private String responseMessage;
    @ApiModelProperty(dataType = "Object", required = true, example = "Response data")
    private RemoveListLimitResponseData responseData;
    @ApiModelProperty(dataType = "Long", required = true, example = "Processed time")
    private Long responseTime;
}

@Getter
@Setter
class RemoveListLimitResponseData {
    private Set<LimitRemoveReponseDto> limitList;
}
