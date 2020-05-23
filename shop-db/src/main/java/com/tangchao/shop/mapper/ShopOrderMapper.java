package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.pojo.ShopOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface ShopOrderMapper extends Mapper<ShopOrder> {

    @Update("UPDATE shop_goods SET stock=stock-#{num} WHERE id=#{id} AND stock >=#{num}")
    int decreaseStock(@Param("id") Long id, @Param("num") Integer num);

    @Select("SELECT " +
            "COUNT(so.order_id) " +
            "FROM " +
            "shop_order so " +
            "LEFT JOIN shop_order_detail sod ON sod.order_id = so.order_id " +
            "WHERE so.status > 1 " +
            "AND so.status != 7 " +
            "AND so.user_code = #{userCode} " +
            "AND sod.goods_id = #{goodsId} ")
    Integer checkLimitEnough(@Param("userCode") Long userCode, @Param("goodsId") Long goodsId);

    @Select("select b.commission,b.integral from shop_order_detail a " +
            "LEFT JOIN shop_goods b on a.goods_id=b.id " +
            "where a.order_id=#{orderId}")
    ShopGoods getGoodsCommission(@Param("orderId") String orderId);

}
