package com.tangchao.web.controller;


import com.tangchao.shop.service.OrderService;
import com.tangchao.shop.vo.OrderVO;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RequestMapping("/order")
@RestController
@Api(value = "元购订单模块", tags = {"元购订单模块"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 元购创建订单
     *
     * @return
     */
    @ApiOperation(value = "提交订单")
    @PostMapping("/createOrder")
    public ResponseEntity<OrderVO> createOrder(@LoginUser Long userCode,@RequestBody Map<String,Object> map, HttpServletRequest request) throws IOException {
        //创建订单
        return ResponseEntity.ok(orderService.createOrder(userCode,map, request));
    }

    @ApiOperation(value = "订单付款")
    @PostMapping("/payOrder")
    public ResponseEntity<Double> payOrder(@ApiParam(value = "订单编号", name = "orderNo") Long orderNo,
                                           @ApiParam(value = "是否自动购买下一期{ 1：是,其他：不是 }", name = "isAutoBuyNext") Integer isAutoBuyNext,
                                           @ApiParam(value = "是否使用福分0:不使用，1使用", name = "isUse") Integer isUse, HttpServletRequest request) throws Exception {

        return ResponseEntity.ok(orderService.commitOrder(isUse,orderNo,isAutoBuyNext,request));
    }
}
