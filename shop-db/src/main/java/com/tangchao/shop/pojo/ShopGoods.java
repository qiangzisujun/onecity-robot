package com.tangchao.shop.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "shop_goods")
@Data
public class ShopGoods {

    @Id
    @KeySql(useGeneratedKeys = true)
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
    private BigDecimal discount;
    private Integer goodsTypeId;
    private Integer isSpecialPrice;
    private Integer isSellWell;
    private Integer isStrict;
    private Integer isReturn;
    private Integer isSpeed;
    private Integer isSpec;
    private Integer isHome;

    private BigDecimal commission;

    private String specList;

    private Integer limitEnough;

    private Integer isVirtual;

    private String virtualMessage;

    @Transient
    private List<ShopSpecGroup> specGroups=new ArrayList<>(0);

    @Transient
    private List<ShopSpecification> specificationList=new ArrayList<>(0);

}
