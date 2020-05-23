package com.tangchao.shop.mapper;

import com.tangchao.shop.pojo.AdvertGroup;
import com.tangchao.shop.pojo.CmsAdvert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CmsAdvertMapper extends Mapper<CmsAdvert> {

    /**
     * @return 广告组列表
     */
    List<AdvertGroup> selectList();

    List<CmsAdvert> selectAdvertList();
}
