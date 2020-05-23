package com.tangchao.shop.vo;

import com.tangchao.shop.pojo.ShopGoods;
import lombok.Data;

import java.util.List;

@Data
public class ShopCategoryVO {

    private List<ShopGoods> ShopGoodsVO;

    private Long id;
    private String name;
}
