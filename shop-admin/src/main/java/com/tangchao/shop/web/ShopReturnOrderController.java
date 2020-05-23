package com.tangchao.shop.web;

import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.params.RejectParam;
import com.tangchao.shop.service.ShopOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @Class ShopReturnOrderController
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/21 16:38
 * @Version 1.0
 **/
@RequestMapping("/api/shop/returnOrder")
@RestController
@Api(value = "钻石商城退款申请模块", tags = {"钻石商城退款申请模块"})
public class ShopReturnOrderController {

    @Autowired
    private ShopOrderService shopOrderService;

    @GetMapping("returnOrderList")//@LoginUser Long userId,
    @ApiOperation(value = "申请退款订单列表")
    public ResponseEntity returnOrderList(@LoginUser Long userId,
                                          @ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                          @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                                          @ApiParam(value = "状态：0、申请中 1、通过 2、驳回 ", name = "status") @RequestParam(value = "status", required = false) Integer status) {
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        return shopOrderService.returnOrderList(pageNo, pageSize, status);
    }

    @ApiOperation(value = "通过订单退款申请")
    @GetMapping("passe")
    public ResponseEntity passe(@LoginUser Long userId,
                                @ApiParam(value = "订单Id", name = "id") @RequestParam("id") String id){
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        return shopOrderService.orderRefund(id);
    }

    // @ApiOperation(value = "退款接口")
    // @GetMapping("adminPasse")
    // public ResponseEntity adminPasse(@ApiParam(value = "退款订单no", name = "orderNo") @RequestParam("orderNo") String orderNo){
    //     return shopOrderService.adminPasse(orderNo);
    // }

    @ApiOperation(value = "驳回订单退款申请")
    @PostMapping("reject")
    public ResponseEntity reject(@LoginUser Long userId,
                                 @RequestBody RejectParam rejectParam){
        if (userId == null) throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        return shopOrderService.reject(rejectParam);
    }

}
