package com.tangchao.web.controller;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.params.GetCouponParam;
import com.tangchao.shop.params.PayCouponParam;
import com.tangchao.shop.pojo.Coupon;
import com.tangchao.shop.pojo.UserCoupon;
import com.tangchao.shop.service.CouponService;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @Class ShopCouponController
 * @Description TODO
 * @Author Aquan
 * @Date 2020.2.15 15:36
 * @Version 1.0
 **/
@Slf4j
@RequestMapping("/api/coupon")
@RestController
@Api(value = "优惠券模块", tags = {"优惠券模块"})
public class ShopCouponController {

    @Autowired
    private CouponService couponService;

    @ApiOperation(value = "优惠券列表")
    @GetMapping("getCouponList")
    public ResponseEntity<PageResult<Coupon>> getCouponList(@ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                                            @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize) {
        return couponService.getCouponList(pageNo, pageSize);
    }

    @ApiOperation(value = "获取优惠券详情信息")
    @GetMapping("getInfo")
    public ResponseEntity getInfo(@ApiParam(value = "优惠券ID", name = "id") @RequestParam("id") Integer id){
        return couponService.getInfo(id);
    }

    @ApiOperation(value = "支付购买优惠券")
    @PostMapping("payCoupon")
    public ResponseEntity payCoupon(@RequestBody PayCouponParam payCouponParam){
        return couponService.payCoupon(payCouponParam);
    }

    @GetMapping("payNotify/{no}")
    public ResponseEntity payNotify(@PathVariable("no") String no,
                                    @RequestParam(value = "result_code") String resultCode,
                                    @RequestParam(value = "shopId") String shopId,
                                    @RequestParam(value = "money") String money,
                                    @RequestParam(value = "type") String type,
                                    @RequestParam(value = "out_trade_no") String outTradeNo,
                                    @RequestParam(value = "orderId") String orderId,
                                    @RequestParam(value = "sign") String sign,
                                    @RequestParam(value = "timeEnd") String timeEnd) throws Exception {
        log.warn("支付回调订单No：" + no + "  resultCode:" + resultCode + "   shopId:" + shopId + "  money:" + money + "  type:" + type + "  outTradeNo:" + outTradeNo + "  orderId:" + orderId + "  sign:" + sign + "  timeEnd:" + timeEnd);
        return couponService.payNotify(no, resultCode, money, type, sign, timeEnd);
    }

    @ApiOperation(value = "个人优惠券列表")
    @GetMapping("getUserCouponList")
    public ResponseEntity<PageResult<UserCoupon>> getUserCouponList(@ApiParam(value = "状态：1、未使用 2、已使用 3、已过期", name = "status", defaultValue = "1") @RequestParam(value = "status", required = false) Integer status,
                                                                    @ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                                                    @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize) {
        return couponService.getUserCouponList(status, pageNo, pageSize);
    }


    @ApiOperation(value = "分享优惠券")
    @GetMapping("shareCoupon")
    public ResponseEntity shareCoupon(@ApiParam(value = "分享的URL", name = "shareUrl") @RequestParam("shareUrl") String shareUrl) {
        return couponService.shareCoupon(shareUrl);
    }

    @ApiOperation(value = "领取分享优惠券")
    @PostMapping("getCoupon")
    public ResponseEntity getCoupon(@RequestBody GetCouponParam getCouponParam) {
        return couponService.getCoupon(getCouponParam);
    }


    @PostMapping("payBob")
    public ResponseEntity<Map<String,String>> userRechargeByPayBoB(@RequestBody PayCouponParam payCouponParam) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return  ResponseEntity.ok(couponService.payOrderByBoB(payCouponParam));
    }

    @ApiOperation("第三方支付回调接口")
    @RequestMapping(value="/userPaymentNotifyByPayBOB",method= RequestMethod.POST)
    public ResponseEntity<String> userPaymentNotifyByPayBOB(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(couponService.userPaymentNotifyByPayBOB(request));
    }


}
