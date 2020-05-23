package com.tangchao.shop.dto;

import com.tangchao.shop.pojo.ShopSpecGroup;
import com.tangchao.shop.pojo.ShopSpecification;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Class ShopGoodsDTO
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/9 15:05
 * @Version 1.0
 **/
@Data
public class ShopGoodsDTO {

    private Long id;
    private String title;
    private String images;
    private Long price;
    private Boolean enable;
    private Date createTime;
    private Date lastUpdateTime;
    private Integer stock; //库存
    private String packingList;
    private Long brandId;
    private Boolean saleable;
    private Long cid;
    private String subTitle;
    private Long integral;
    private Long specParam;
    private String imagesInfo;
    private String thumbnail;
    private Long priceForme;
    private Integer sort;
    private Integer datalevel;
    private Integer salesVolume;
    private Integer goodsTypeId;
    private Integer isSpecialPrice;
    private Integer isSellWell;
    private Integer isStrict;
    private Integer isReturn;
    private Integer isSpeed;
    private Integer isHome;
    private BigDecimal discount;//最高可以使用抵扣优惠券多少元

    private String specList;
    private Object specListObject;
    private Integer limitEnough;
    private Integer isVirtual;
    private String virtualMessage;

    private List<ShopSpecGroup> specGroupList;//规格组

    private List<ShopSpecification>  specificationList;//规格属性列表

    private BigDecimal commission;//每个商品的分销提点

    public List<String> getImages() {
        List<String> list = new ArrayList<>();
        if (this.images.contains(",")) list.addAll(Arrays.asList(images.split(",")));//根据逗号分隔转化为list
        else list.add(this.images);
        return list;
    }

    public List<String> getImagesInfo() {
        List<String> list = new ArrayList<>();
        if (this.imagesInfo.contains(",")) list.addAll(Arrays.asList(imagesInfo.split(",")));//根据逗号分隔转化为list
        else list.add(this.imagesInfo);
        return list;
    }

}
