package com.tangchao.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel
public class GoodsTypeVO implements Serializable {

    @ApiModelProperty(value = "分类Id", name = "id")
    private Long id;

    @ApiModelProperty(value = "分类名称", name = "typeName")
    private String typeName;

    @ApiModelProperty(value = "分类总数", name = "countNum")
    private Integer countNum;

    @ApiModelProperty(value = "商品期数Id", name = "stageIds")
    private String stageIds;

    @ApiModelProperty(value = "分类名称", name = "typeName")
    private String typeNameCn;

    @ApiModelProperty(value = "分类名称", name = "typeName")
    private String typeNameMa;
}
