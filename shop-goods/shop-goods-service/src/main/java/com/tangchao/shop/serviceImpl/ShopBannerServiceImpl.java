package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.ShopBannersMapper;
import com.tangchao.shop.pojo.ShopBanners;
import com.tangchao.shop.service.ShopBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopBannerServiceImpl implements ShopBannerService {


    @Autowired
    private ShopBannersMapper bannersMapper;

    @Override
    public PageResult<ShopBanners> getShopBanners() {
        ShopBanners banners = new ShopBanners();
        banners.setStatus(1L);
        PageHelper.startPage(0, 3, true);
        List<ShopBanners> list = bannersMapper.select(banners);
        PageInfo<ShopBanners> pageInfo = new PageInfo<ShopBanners>(list);
        return new PageResult<>(pageInfo.getTotal(), list);
    }
}
