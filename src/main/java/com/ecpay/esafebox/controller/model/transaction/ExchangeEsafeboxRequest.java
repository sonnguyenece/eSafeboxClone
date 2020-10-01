package com.ecpay.esafebox.controller.model.transaction;

import com.ecpay.esafebox.controller.model.BaseRequest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class ExchangeEsafeboxRequest extends BaseRequest{
	@ApiModelProperty(dataType = "String", notes = "Box clientKey, dung de mo Box", required = true, example = "iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAABHNCSVQICAgIfAhkiAAAAi9JREFUOE+tVd2R2jAQ3pU8Hh7JIzNgiQqACsJVEFLBQQVcKjhSQS4VHK4gpAM6OFMBtmGGx+TZY3kzy2g5n/m5ZCZ6lLSf9/vRGuGdlaZpgogDvkZEa2vt3a0SvHW42+3GzrmVtba93W7bSqlfYRj2O51Oeq3uDWCWZUsAuAeARRiGcVEUcyIaWWvHDOC7fdFaf3fO8b0HAFgaY2bygRMggxHRUGv94Jx78jR/aq0X3W434YI8zydENAWAT0S0CYJgWpblEhFfBLQOSFrrERczvVar1b5G7XA4WDnb7/dD5xwDHrGaHZq66NxRVVWPiDj0piRKqa9RFK2EopdhbYxh+q+A8iURvaZnjIhrD8hasnZH3bjToii2wuwE6A8eAeCDMWbitfqBiJ/r3dR0PJ1lWcbdMmjMcmGapmtE/MgiK6UWDOBpJMYYNuBseQMH1tqRj9bCYySYZRkppe56vd6RFi/eQ8RZFEUco7OV5/mUiJ7FiDd1/x2Q6QGAAYAkCIIvrMNfUh5aa4c+CXNEHLNsx9iwDlVVTQDANkw5oy10xTBvSqq1Xh5NEf4SgQuxYR1FX44NGxWzYVdj441YEBE/rXG/3/8tEamqih2UaXNKwrvBbppTf15Nm//l6Q2CIJiVZ==")
	String keyBoxClient;
	@ApiModelProperty(dataType = "String", notes = "Id thiet bi", required = true)
	String terminalId;
	@ApiModelProperty(dataType = "String", notes = "Thong tin thiet bi", required = false)
	String terminalInfo;
	@ApiModelProperty(dataType = "String", notes = "So vi edong gui", required = true, example = "0987114851")
	String sender;
	@ApiModelProperty(dataType = "Long", notes = "So serial Box nhan", required = true, example = "8934288517")
	String receiver;
	@ApiModelProperty(dataType = "String", notes = "Noi dung giao dich", required = true)
	String content;
	@ApiModelProperty(dataType = "Array", notes = "So vi edong gui", required = true, example = "[{\"issuerCode\": \"EGOLD\",\"values\": [10, 20],\"quantities\": [3, 5]},{\"issuerCode\": \"ECPAY\",\"values\": [500000],\"quantities\": [8]}]")
	Object items;
	
}
