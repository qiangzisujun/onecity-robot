package com.tangchao.shop.web;

import com.tangchao.shop.service.CouponLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Class ShopCouponOrderController
 * @Description TODO
 * @Author Aquan
 * @Date 2020/2/27 17:45
 * @Version 1.0
 **/
@RequestMapping("/api/shopCouponOrder")
@RestController
@Api(value = "钻石商城优惠券管理模块", tags = {"钻石商城优惠券管理模块"})
public class ShopCouponOrderController {

    @Autowired
    private CouponLogService couponLogService;

    @ApiOperation(value = "优惠券订单列表")
    @GetMapping("getCouponOrderList")
    public ResponseEntity getCouponOrderList(@ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                             @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                                             @ApiParam(value = "是否支付（0/未付款 1/已付款）", name = "logStatus", defaultValue = "1") @RequestParam(value = "logStatus", required = false) Integer logStatus,
                                             @ApiParam(value = "是否使用（0/未使用 1/已使用）", name = "couponStatus", defaultValue = "0") @RequestParam(value = "couponStatus", required = false) Integer couponStatus,
                                             @ApiParam(value = "用户电话号码", name = "userMobile") @RequestParam(value = "userMobile", required = false) String userMobile,
                                             @ApiParam(value = "日期之前", name = "beforeDate") @RequestParam(value = "beforeDate", required = false) Long beforeDate,
                                             @ApiParam(value = "日期之后", name = "rearDate") @RequestParam(value = "rearDate", required = false) Long rearDate) {
        return couponLogService.getCouponOrderList(pageNo, pageSize, logStatus, userMobile, couponStatus, beforeDate, rearDate);
    }





}
