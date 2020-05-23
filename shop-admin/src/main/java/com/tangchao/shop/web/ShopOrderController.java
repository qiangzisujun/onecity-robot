package com.tangchao.shop.web;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.params.ChangeAddressParam;
import com.tangchao.shop.params.DeliveryParam;
import com.tangchao.shop.params.ShopPayOrderParam;
import com.tangchao.shop.service.ShopOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Class ShopOrderController
 * @Description TODO
 * @Author Aquan
 * @Date 2020/1/10 16:33
 * @Version 1.0
 **/
@RequestMapping("/api/shop/order")
@RestController
@Api(value = "钻石商城订单模块", tags = {"钻石商城订单模块"})
public class ShopOrderController {

    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("list")//@LoginUser Long userId,
    @ApiOperation(value = "订单列表")
    public ResponseEntity list(@LoginUser Long userId,
                               @ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                               @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                               @ApiParam(value = "状态：1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价,7,用户已取消", name = "status") @RequestParam(value = "status", required = false) Integer status,
                               @ApiParam(value = "订单单号", name = "orderNo") @RequestParam(value = "orderNo", required = false) String orderNo,
                               @ApiParam(value = "买家昵称", name = "buyerNick") @RequestParam(value = "buyerNick", required = false) String buyerNick,
                               @ApiParam(value = "收货人名称", name = "username") @RequestParam(value = "username", required = false) String username,
                               @ApiParam(value = "用户Code", name = "userCode") @RequestParam(value = "userCode", required = false) String userCode,
                               @ApiParam(value = "手机号", name = "phone") @RequestParam(value = "phone", required = false) String phone,
                               @ApiParam(value = "日期之前", name = "beforeDate") @RequestParam(value = "beforeDate", required = false) Long beforeDate,
                               @ApiParam(value = "日期之后", name = "rearDate") @RequestParam(value = "rearDate", required = false) Long rearDate,
                               @ApiParam(value = "订单类型：0普通订单 1虚拟直充订单", name = "orderType") @RequestParam(value = "orderType", required = false) Integer orderType) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        return shopOrderService.list(pageNo, pageSize, status, orderNo, buyerNick, username, userCode, phone, beforeDate, rearDate, orderType);
    }

    @PostMapping("delivery")
    @ApiOperation(value = "发货操作")
    public ResponseEntity delivery(@LoginUser Long userId,
                                   @RequestBody DeliveryParam deliveryParam) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        return shopOrderService.delivery(deliveryParam);
    }

    @PostMapping("changeAddress")
    @ApiOperation(value = "修改发货地址")
    public ResponseEntity changeAddress(@LoginUser Long userId,
                                        @RequestBody ChangeAddressParam changeAddressParam) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        return shopOrderService.changeAddress(changeAddressParam);
    }

    @GetMapping("getById/{id}")
    public ResponseEntity getById(@PathVariable("id") String id) {
        return shopOrderService.getById(id);
    }


//    @PostMapping("pay")
//    public ResponseEntity pay(@RequestBody ShopPayOrderParam payOrderParam) {
//        return shopOrderService.pay(payOrderParam);
//    }


}
