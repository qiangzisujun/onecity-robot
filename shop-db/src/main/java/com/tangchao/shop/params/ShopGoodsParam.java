package com.tangchao.shop.params;

import com.tangchao.shop.pojo.ShopSpecGroup;
import com.tangchao.shop.pojo.ShopSpecParam;
import com.tangchao.shop.pojo.ShopSpecification;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Class ShopGoodsParam
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/8 15:04
 * @Version 1.0
 **/
@Data
@ApiModel
public class ShopGoodsParam {

    @ApiModelProperty(value = "商品id", name = "id")
    private Long id;

    @ApiModelProperty(value = "商品标题", name = "title")
    private String title;

    @ApiModelProperty(value = "商品图片", name = "images")
    private List<String> images;

    @ApiModelProperty(value = "商品售价", name = "price")
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

    @ApiModelProperty(value = "购买商品获得钻石", name = "integral")
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

    @ApiModelProperty(value = "此商品最高可使用xx元优惠", name = "discount")
    private BigDecimal discount;

    @ApiModelProperty(value = "钻石商城商品分类id", name = "goodsTypeId")
    private Integer goodsTypeId;

    @ApiModelProperty(value = "是否是限时秒杀，0不是，1是", name = "isSpecialPrice")
    private Integer isSpecialPrice;

    @ApiModelProperty(value = "是否是热销榜，0不是，1是", name = "isSellWell")
    private Integer isSellWell;

    @ApiModelProperty(value = "是否是严选产品，0不是，1是", name = "isStrict")
    private Integer isStrict;

    @ApiModelProperty(value = "是否是七天可退换，0不是，1是", name = "isReturn")
    private Integer isReturn;

    @ApiModelProperty(value = "是否是极速发货，0不是，1是", name = "isSpeed")
    private Integer isSpeed;

    @ApiModelProperty(value = "是否是首页推荐，0不是，1是", name = "isHome")
    private Integer isHome;

    @ApiModelProperty(value = "商品规格列表", name = "specificationList")
    private List<ShopSpecification> specificationList;

    @ApiModelProperty(value = "是否使用规格", name = "isSpec")
    private Integer isSpec;

    @ApiModelProperty(value = "规格集合", name = "isSpec")
    private List<ShopSpecGroup> specGroups;

    @ApiModelProperty(value = "分享提成", name = "commission")
    private BigDecimal commission;

    @ApiModelProperty(value = "规格属性(json格式)", name = "specList")
    private String specList;

    @ApiModelProperty(value = "限购：0/无限购，其他根据值进行限购", name = "limitEnough")
    private Integer limitEnough;

    @ApiModelProperty(value = "是否虚拟充值商品，0不是，1是", name = "isVirtual")
    private Integer isVirtual;

    @ApiModelProperty(value = "虚拟账号充值提示", name = "virtualMessage")
    private String virtualMessage;



    public String getImages() {
        String str = StringUtils.join(this.images, ",");
        return str;
    }

    public String getImagesInfo() {
        String str = StringUtils.join(this.imagesInfo, ",");
        return str;
    }

}
