package com.tangchao.shop.pojo;


import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "shop_banners")
@Data
public class ShopBanners implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String linkUrl;
    private String bannerIndex;
    private String bannerDescription;
    private String images;
    private String createTime;
    private Long status;
}
