package com.tangchao.shop.vo;

import com.tangchao.shop.pojo.ShopBanners;
import com.tangchao.shop.pojo.ShopCategory;
import lombok.Data;

import java.util.List;

@Data
public class BannerAndCategory {

    public List<ShopCategory> categories;

    public List<ShopBanners> banners;
}