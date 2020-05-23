package com.tangchao.shop.serviceImpl;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.dto.ShopCartDTO;
import com.tangchao.shop.dto.ShopCartsDTO;
import com.tangchao.shop.interceptor.UserInterceptor;
import com.tangchao.shop.mapper.CustomerMapper;
import com.tangchao.shop.mapper.ShopCartMapper;
import com.tangchao.shop.mapper.ShopGoodsMapper;
import com.tangchao.shop.mapper.ShopSpecParamMapper;
import com.tangchao.shop.params.ShopCartParam;
import com.tangchao.shop.params.UpdateCarNumParam;
import com.tangchao.shop.pojo.ShopCart;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.pojo.UserInfo;
import com.tangchao.shop.service.ShopCartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class ShopCartServiceImpl implements ShopCartService {

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ShopSpecParamMapper paramMapper;

    @Autowired
    private ShopGoodsMapper goodsMapper;

    @Override
    public void insertCart(ShopCartDTO cart) {
        if (StringUtils.isBlank(cart.getGoodsId().toString()) || StringUtils.isBlank(cart.getUserCode().toString())) {
            throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        }

        //判断是否存在购物车
        ShopCart cartInfo = new ShopCart();
        cartInfo.setGoodsId(cart.getGoodsId());
        cartInfo.setUserCode(cart.getUserCode());
        cartInfo = shopCartMapper.selectOne(cartInfo);
        if (cartInfo != null) {//存在数据库，则修改数量
            cartInfo.setNumber(cart.getNumber() + cartInfo.getNumber());
            shopCartMapper.updateByPrimaryKey(cartInfo);
        } else {
            ShopCart shopCart = new ShopCart();
            shopCart.setAddTime(new Date());
            shopCart.setGoodsId(cart.getGoodsId());
            shopCart.setGoodsName(cart.getGoodsName());
            shopCart.setImage(cart.getImage());
            shopCart.setNumber(cart.getNumber());
            shopCart.setUserCode(cart.getUserCode());
            shopCart.setPrice(cart.getPrice());
            shopCart.setSpecifications(cart.getSpecifications());
            shopCart.setStatus(1);
            shopCartMapper.insertSelective(shopCart);
        }
    }

    @Override
    public ShopCartsDTO queryCartList() {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        ShopCart cart = new ShopCart();
        cart.setUserCode(user.getUserCode());
        cart.setStatus(1);
        List<ShopCart> shopCarts = shopCartMapper.select(cart);
        ShopCartsDTO shopCartsDTO = new ShopCartsDTO();
        shopCartsDTO.setTotal((long) 0);
        shopCartsDTO.setTotalIntegral((long) 0);
        shopCartsDTO.setShopCartList(shopCarts);
        for (ShopCart shopCart : shopCarts) {
            Long total = shopCartsDTO.getTotal();
            Long price = shopCart.getPrice() * shopCart.getNumber();
            shopCartsDTO.setTotal(total += price);
            Long totalIntegral = shopCartsDTO.getTotalIntegral();
            Long integral = shopCart.getIntegral() * shopCart.getNumber();
            shopCartsDTO.setTotalIntegral(totalIntegral += integral);
        }
        return shopCartsDTO;
    }

    /**
     * 修改购物车数量
     *
     * @param updateCarNumParam
     */
    @Override
    public ResponseEntity updateNum(UpdateCarNumParam updateCarNumParam) {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        if (updateCarNumParam.getCarId() == null || updateCarNumParam.getNum() == null) throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        ShopCart shopCart = shopCartMapper.selectByPrimaryKey(updateCarNumParam.getCarId());
        Integer total = shopCart.getNumber() + updateCarNumParam.getNum();
        if (total == 0) shopCart.setStatus(0);
        else shopCart.setNumber(total);
        Integer count = shopCartMapper.updateByPrimaryKeySelective(shopCart);
        if (count.toString().equals("1")) return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 删除购物车商品
     *
     * @param cartId
     * @return
     */
    @Override
    public ResponseEntity deleteCart(Long cartId) {
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        if (cartId == null) throw new CustomerException(ExceptionEnum.INVALID_CART_DATA_TYPE);
        ShopCart shopCart = new ShopCart();
        shopCart.setId(cartId);
        shopCart.setStatus(0);
        Integer count = shopCartMapper.updateByPrimaryKeySelective(shopCart);
        if (count.toString().equals("1")) return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 清空购物车
     * @return
     */
    @Override
    public ResponseEntity clearCart() {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopCart cartInfo = new ShopCart();
        cartInfo.setUserCode(user.getUserCode());
        List<ShopCart> cartList = shopCartMapper.select(cartInfo);
        if (CollectionUtils.isEmpty(cartList)) {
            throw new CustomerException(ExceptionEnum.CART_NOT_FOUND);
        }
        for (ShopCart shopCart : cartList) {
            shopCart.setStatus(0);
            shopCartMapper.updateByPrimaryKeySelective(shopCart);
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity addCart(ShopCartParam shopCartParam) {
        //获取用户登录
        UserInfo user = UserInterceptor.getUserInfo();
        if (user== null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        ShopCart cart = new ShopCart();
        cart.setUserCode(user.getUserCode());
        cart.setStatus(1);
        List<ShopCart> shopCarts = shopCartMapper.select(cart);
        for (ShopCart shopCart : shopCarts) {
            Long goodsId = shopCart.getGoodsId();
            if (goodsId.toString().equals(shopCartParam.getGoodId().toString())) {
                ShopCart shopCart1 = new ShopCart();
                shopCart1.setId(shopCart.getId());
                shopCart1.setNumber(shopCart.getNumber() + shopCartParam.getNumber());
                Integer i = shopCartMapper.updateByPrimaryKeySelective(shopCart1);
                if (i.toString().equals("1")) return ResponseEntity.ok().build();
                else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        if (!StringUtils.isNotBlank(shopCartParam.getGoodId().toString())) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        ShopGoods shopGoods = goodsMapper.selectByPrimaryKey(shopCartParam.getGoodId());
        ShopCart shopCart = new ShopCart();
        shopCart.setUserCode(user.getUserCode());
        shopCart.setGoodsId(shopCartParam.getGoodId());
        shopCart.setGoodsName(shopGoods.getTitle());
        shopCart.setPrice(shopGoods.getPrice());
        shopCart.setNumber(shopCartParam.getNumber());
        shopCart.setImage(shopGoods.getImages().split(",")[0]);
        shopCart.setIntegral(shopGoods.getIntegral());
        Integer count = shopCartMapper.insertSelective(shopCart);
        if (count.toString().equals("1")) return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
