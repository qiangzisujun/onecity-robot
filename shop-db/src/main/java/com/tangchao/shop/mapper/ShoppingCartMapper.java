package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShoppingCart;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface ShoppingCartMapper extends Mapper<ShoppingCart> {

    /**
     * 查询已购买过的商品次数
     *
     * @param userCode     用户编号
     * @param goodsStageId 商品期次Id
     * @return null（未购买）or 购买次数
     */
    Long selectPurchasedGoodsNum(
            @Param(value = "userCode") Long userCode,
            @Param(value = "goodsStageId") Long goodsStageId);

    List<ShoppingCart> getShoppingCartByIds(Map<String, Object> map);

    int countCartByUserCode(@Param("userCode") Long userCode);
}
