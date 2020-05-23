package com.tangchao.shop.service;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.ShoppingCart;
import com.tangchao.shop.vo.CartVO;
import com.tangchao.shop.vo.GoodsStageVO;

import java.util.List;
import java.util.Map;

public interface ShoppingCartService {

    /**
     * 元购-加入购物车
     *
     * @param stageId
     */
    void addCart(Long stageId,Integer num);

    /**
     * 修改购物车数量
     *
     * @param goodsNo
     * @param num
     */
    void updateCartNum(Long userId,String goodsNo, Integer num);

    /**
     * 删除选中购物车
     *
     * @param goodsId
     */
    void deleteCart(List<String> goodsId);

    /**
     * 清空用户购物车
     */
    void clearCart();

    PageResult<CartVO> queryCartList(Integer pageNo, Integer pageSize);

    Integer countCartByUserCode(Long userCode);

    void addCartByBatch(Long userCode, Map<String, Object> data);

    void addCartByGoodsNo(Long userCode,String goodsNo);

    void test();

    int deleteShopCartByList(List<ShoppingCart> list);
}
