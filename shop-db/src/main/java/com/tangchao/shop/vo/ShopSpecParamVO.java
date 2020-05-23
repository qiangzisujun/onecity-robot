package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ShopSpecParamVO {

    @ApiModelProperty(value = "规格Id", name = "id")
    private Long id;

    @ApiModelProperty(value = "规格名称", name = "name")
    private String name;
}
