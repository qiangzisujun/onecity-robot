package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.GoodsRcmd;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface GoodsRcmdMapper extends Mapper<GoodsRcmd> {

    @Update("update goods_rcmd set flag = -1,last_modify_time = now() where flag = 0 and goods_id = #{goodsId} ")
    int deleteRcmdByGoodsId(@Param(value = "goodsId") Long goodsId);

    @Update("update goods_rcmd set last_modify_time = now(),goods_hot=#{goodsHot} where flag = 0 and goods_id = #{goodsId} ")
    int updateBygoodsRcmd(@Param(value = "goodsId") Long goodsId,@Param(value = "goodsHot") Integer goodsHot);
}
