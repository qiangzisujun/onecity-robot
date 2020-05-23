package com.tangchao.shop.vo;

import com.tangchao.shop.pojo.ShopSpecParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel
public class ShopGoodsVO implements Serializable {

    @ApiModelProperty(value = "商品id", name = "id")
    private Long id;

    @ApiModelProperty(value = "商品标题", name = "title")
    private String title;

    @ApiModelProperty(value = "商品图片", name = "images")
    private String images;

    @ApiModelProperty(value = "商品价格", name = "price")
    private Long price;

    @ApiModelProperty(value = "商品库存", name = "stock")
    private Integer stock; //库存

    @ApiModelProperty(value = "商品包装清单", name = "packingList")
    private String packingList;

    @ApiModelProperty(value = "商品分类Id", name = "cid")
    private Long cid;

    @ApiModelProperty(value = "商品付款人数", name = "salesVolume")
    private Integer salesVolume;

    @ApiModelProperty(value = "商品简介", name = "subTitle")
    private String subTitle;

    @ApiModelProperty(value = "购买商品获得积分", name = "integral")
    private Long integral;

    private List<ShopSpecParam> specParams;

    @ApiModelProperty(value = "商品详情图", name = "imagesInfo")
    private List<String> imagesInfo;

    @ApiModelProperty(value = "商品缩略图", name = "thumbnail")
    private String thumbnail;

    @ApiModelProperty(value = "商品原价", name = "priceForme")
    private Long priceForme;

    @ApiModelProperty(value = "商品排序号", name = "sort")
    private Integer sort;

}
