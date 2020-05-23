package com.tangchao.web.controller;

import com.tangchao.shop.dto.ShopCartsDTO;
import com.tangchao.shop.params.DeleteCartGoodParam;
import com.tangchao.shop.params.ShopCartParam;
import com.tangchao.shop.params.UpdateCarNumParam;
import com.tangchao.shop.service.ShopCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Api(value = "钻石商城购物车模块", tags = {"钻石商城购物车模块"})
public class ShopCartController {

    @Autowired
    private ShopCartService shopCartService;

    @PostMapping("addCart")
    @ApiOperation(value = "添加购物车")
    public ResponseEntity addCart(@RequestBody ShopCartParam shopCartParam) {
        return shopCartService.addCart(shopCartParam);
    }

    @ApiOperation(value = "查询购物车")
    @GetMapping("queryCartList")
    public ResponseEntity<ShopCartsDTO> queryCartList() {
        return ResponseEntity.ok(shopCartService.queryCartList());
    }

    @ApiOperation(value = "修改购物车数量")
    @PostMapping("updateCarNum")
    public ResponseEntity updateCarNum(@RequestBody UpdateCarNumParam updateCarNumParam) {
        return shopCartService.updateNum(updateCarNumParam);
    }

    @ApiOperation(value = "删除购物车的单项商品")
    @PostMapping("deleteCart")
    public ResponseEntity deleteCart(@RequestBody DeleteCartGoodParam deleteCartGoodParam) {
        return shopCartService.deleteCart(deleteCartGoodParam.getCartId());
    }

    @ApiOperation(value = "清空购物车")
    @PostMapping("clearCart")
    public ResponseEntity clearCart() {
        return shopCartService.clearCart();
    }

}
