package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class CustomerAddressVO implements Serializable {
    //  省
    @ApiModelProperty(value = "省", name = "province")
    private String province;
    //  市
    @ApiModelProperty(value = "市", name = "city")
    private String city;
    //  区
    @ApiModelProperty(value = "区", name = "area")
    private String area;
    //  街道
    @ApiModelProperty(value = "街道", name = "street")
    private String street;
    //  详细地址
    @ApiModelProperty(value = "详细地址", name = "detailed")
    private String detailed;
    //  邮编
    @ApiModelProperty(value = "邮编", name = "zipCode")
    private String zipCode;
    //  收货人名称
    @ApiModelProperty(value = "收货人名称", name = "userName")
    private String userName;
    //  收货人手机号码
    @ApiModelProperty(value = "收货人手机号码", name = "userMobile")
    private String userMobile;
    //  是否默认{ 0：非默认，1：默认 }
    @ApiModelProperty(value = "是否默认{ 0：非默认，1：默认 }", name = "isDefault")
    private Integer isDefault;

    @ApiModelProperty(value = "省编码", name = "provinceCode")
    private String provinceCode;

    @ApiModelProperty(value = "市编码", name = "cityCode")
    private String cityCode;

    @ApiModelProperty(value = "区编码", name = "areaCode")
    private String areaCode;

    @ApiModelProperty(value = "收货地址Id", name = "areaCode")
    private Integer id;
}
