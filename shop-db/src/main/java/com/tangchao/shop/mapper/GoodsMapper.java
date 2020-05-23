package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.Goods;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface GoodsMapper extends Mapper<Goods> {
    List<Goods> goodsList(String goodsName, Integer typeId);

    List<Goods> selectSellGoodsList(@Param("goodsName") String goodsName, @Param("typeId")Integer typeId);

    Integer updateSellgoods(@Param("id") Long id, @Param("isNew") Integer isNew, @Param("goodsHot") Integer goodsHot, @Param("isRcmd") Integer isRcmd);

    /**
     * 查询商品数据
     * @return List<Map<String, Object>>
     */
    List<Map<String, Object>> selectByMap();
}
