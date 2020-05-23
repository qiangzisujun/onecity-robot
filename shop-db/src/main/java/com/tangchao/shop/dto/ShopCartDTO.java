package com.tangchao.shop.dto;

import com.tangchao.shop.vo.ShopSpecParamVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ShopCartDTO {

    @ApiModelProperty(value = "购物车Id", name = "id")
    private Long id;

    @ApiModelProperty(value = "用户Id", name = "userId")
    private Long userCode;// 用户id

    @ApiModelProperty(value = "商品id", name = "goodsId")
    private Long goodsId;// 商品id

    @ApiModelProperty(value = "标题", name = "goodsName")
    private String goodsName;// 标题

    @ApiModelProperty(value = "图片", name = "image")
    private String image;// 图片

    @ApiModelProperty(value = "加入购物车时的价格", name = "price")
    private Long price;// 加入购物车时的价格

    @ApiModelProperty(value = "购买数量", name = "number")
    private Integer number;// 购买数量

    @ApiModelProperty(value = "规格", name = "specifications")
    private Long specifications;

    @ApiModelProperty(value = "规格名称", name = "specificationsName")
    private String specificationsName;

    @ApiModelProperty(value = "购买商品获得积分", name = "integral")
    private Long integral;

    @ApiModelProperty(value = "规格对象", name = "spec")
    private ShopSpecParamVO spec;

}
