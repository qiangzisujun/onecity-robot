package com.tangchao.shop.service;

import com.tangchao.shop.dto.ShopCartDTO;
import com.tangchao.shop.dto.ShopCartsDTO;
import com.tangchao.shop.params.ShopCartParam;
import com.tangchao.shop.params.UpdateCarNumParam;
import org.springframework.http.ResponseEntity;

public interface ShopCartService {

    void insertCart(ShopCartDTO cart);

    ShopCartsDTO queryCartList();

    ResponseEntity updateNum(UpdateCarNumParam updateCarNumParam);

    ResponseEntity deleteCart(Long cartId);

    ResponseEntity clearCart();

    ResponseEntity addCart(ShopCartParam shopCartParam);
}
