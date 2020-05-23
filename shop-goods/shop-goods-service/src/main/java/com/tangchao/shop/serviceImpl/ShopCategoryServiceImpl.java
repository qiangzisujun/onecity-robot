package com.tangchao.shop.serviceImpl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.mapper.ShopCategoryMapper;
import com.tangchao.shop.mapper.ShopGoodsMapper;
import com.tangchao.shop.pojo.ShopCategory;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.service.ShopCategoryService;
import com.tangchao.shop.vo.ShopCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {

    @Autowired
    private ShopCategoryMapper shopCategoryMapper;

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Override
    public PageResult<ShopCategory> getShopCategoryListByPage() {
        ShopCategory category = new ShopCategory();
        category.setIsParent(true);
        category.setStatus(1L);
        PageHelper.startPage(0, 8, true);
        List<ShopCategory> list = shopCategoryMapper.select(category);
        PageInfo<ShopCategory> pageInfo = new PageInfo<ShopCategory>(list);
        return new PageResult<>(pageInfo.getTotal(), list);
    }

    @Override
    public List<ShopCategoryVO> getShopCategoryList(Long cateId) {
        ShopCategory category = new ShopCategory();
        category.setStatus(1L);
        if (cateId != null) {
            category.setId(cateId);
        } else {
            category.setIsParent(true);
            category.setStatus(1L);
        }
        List<ShopCategory> list = shopCategoryMapper.select(category);
        List<ShopCategoryVO> categoryVOS = new ArrayList<>();

        Map<Long, List<ShopGoods>> goodsMap = new HashMap<>();//键分类，值分类的商品
        for (ShopCategory cate : list) {
            ShopGoods goods = new ShopGoods();
            goods.setCid(cate.getId());
            goodsMap.put(cate.getId(), goodsMapper.select(goods));
            ShopCategoryVO vo = new ShopCategoryVO();
            vo.setId(cate.getId());
            vo.setName(cate.getName());
            categoryVOS.add(vo);
        }
        for (ShopCategoryVO vo : categoryVOS) {
            vo.setShopGoodsVO(goodsMap.get(vo.getId()));
        }
        return categoryVOS;
    }
}
