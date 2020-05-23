package com.tangchao.web.controller;


import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.service.ShoppingCartService;
import com.tangchao.shop.vo.CartVO;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopCart")
@Api(value = "元购购物车模块", tags = {"元购购物车模块"})
public class CartController {

    @Autowired
    private ShoppingCartService cartService;

    @PostMapping("/addCart")
    @ApiOperation(value = "添加购物车")
    public ResponseEntity<Void> addCart(@ApiParam(value = "商品期数Id", name = "stageId") Long stageId) {
        cartService.addCart(stageId,1);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "查询购物车")
    @GetMapping("/queryCartList")
    public ResponseEntity<PageResult<CartVO>> queryCartList(@ApiParam(value = "页数", name = "pageNo")
                                                            @RequestParam(value = "pageNo") Integer pageNo,
                                                            @ApiParam(value = "页数大小", name = "pageSize")
                                                            @RequestParam(value = "pageSize") Integer pageSize) {
        return ResponseEntity.ok(cartService.queryCartList(pageNo, pageSize));
    }


    @ApiOperation(value = "修改购物车数量")
    @GetMapping("/updateCarNum")
    public ResponseEntity<Void> updateCarNum(@LoginUser Long userId, @RequestParam("goodsId") String goodsId, @RequestParam("num") Integer num) {
        cartService.updateCartNum(userId,goodsId, num);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "删除购物车")
    @PostMapping("/deleteCart")
    public ResponseEntity<Void> deleteCart(@LoginUser Long userCode, @RequestBody Map<String, Object> data) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        List<String> list = (List<String>) data.get("data");
        cartService.deleteCart(list);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "清空购物车")
    @PostMapping("/clearCart")
    public ResponseEntity<Void> clearCart(@LoginUser Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        cartService.clearCart();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "购物车总数")
    @GetMapping("/countCartByUserCode")
    public ResponseEntity<Integer> countCartByUserCode(@LoginUser Long userCode){
        return ResponseEntity.ok(cartService.countCartByUserCode(userCode));
    }

    @PostMapping("/addCartByNum")
    @ApiOperation(value = "添加购物车按数量")
    public ResponseEntity<Void> addCart(@RequestBody Map<String,Object> data) {
        Long stageId=Long.valueOf(data.get("stageId").toString());
        Integer num=Integer.valueOf(data.get("num").toString());
        cartService.addCart(stageId,num);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/addCartByBatch")
    @ApiOperation(value = "一键添加购物车")
    public ResponseEntity<Void> addCartByBatch(@LoginUser Long userCode,@RequestBody Map<String,Object> data) {
        cartService.addCartByBatch(userCode,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/addCartByGoodsNo")
    @ApiOperation(value = "添加购物车")
    public ResponseEntity<Void> addCartByGoodsNo(@LoginUser Long userCode,String goodsNo) {
        cartService.addCartByGoodsNo(userCode,goodsNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
