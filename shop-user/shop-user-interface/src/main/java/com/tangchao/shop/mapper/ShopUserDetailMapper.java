package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.ShopUserDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.BaseMapper;

public interface ShopUserDetailMapper extends BaseMapper<ShopUserDetail> {

    @Update("UPDATE shop_user_detail SET integral=integral+#{integral} WHERE user_code=#{userCode} AND integral >=0")
    int updateIntegral(@Param("userCode") Long userCode, @Param("integral") Integer integral);
}
