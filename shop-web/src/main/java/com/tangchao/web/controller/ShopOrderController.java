package com.tangchao.web.controller;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.ShopOrderDTO;
import com.tangchao.shop.params.*;
import com.tangchao.shop.service.ShopOrderService;
import com.tangchao.shop.vo.OrderResponse;
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

@Slf4j
@RequestMapping("/api/order")
@RestController
@Api(value = "钻石商城订单模块", tags = {"钻石商城订单模块"})
public class ShopOrderController {

    @Autowired
    private ShopOrderService shopOrderService;

    /**
     * 创建订单
     *
     * @return
     */
    @ApiOperation(value = "提交订单")
    @PostMapping("createOrder")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody ShopOrderParam shopOrderParam, HttpServletRequest request) {
        //创建订单
        return ResponseEntity.ok(shopOrderService.createOrder(shopOrderParam, request));
    }

    @ApiOperation(value = "订单列表")
    @GetMapping("queryOrderList")
    public ResponseEntity<PageResult<ShopOrderDTO>> queryOrderList(@ApiParam(value = "状态：1、未付款 2、已付款,未发货 3、已发货,未确认 4、交易成功 5、交易关闭 6、已评价,7,用户已取消", name = "status") @RequestParam(value = "status", required = false) Integer status,
                                                                   @ApiParam(value = "页数(第几页)", name = "pageNo" , defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                                                   @ApiParam(value = "页数大小(每页有多少条)", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(shopOrderService.getShopOrderList(status, pageNo, pageSize));
    }

    @ApiOperation(value = "订单详情")
    @GetMapping("getBy")
    public ResponseEntity getBy(@ApiParam(value = "订单id", name = "orderId") @RequestParam(value = "orderId") String orderId) {
        return shopOrderService.getBy(orderId);
    }

    @ApiOperation(value = "取消订单")
    @GetMapping("cancelOrder")
    public ResponseEntity cancelOrder(@ApiParam(value = "订单id", name = "orderId") @RequestParam(value = "orderId") String orderId) {
        return shopOrderService.cancelOrder(orderId);
    }

    @GetMapping("payNotify/{id}")
    public ResponseEntity<Object> payNotify(@PathVariable("id") String id,
                                            @RequestParam(value = "result_code") String resultCode,
                                            @RequestParam(value = "shopId") String shopId,
                                            @RequestParam(value = "money") String money,
                                            @RequestParam(value = "type") String type,
                                            @RequestParam(value = "out_trade_no") String outTradeNo,
                                            @RequestParam(value = "orderId") String orderId,
                                            @RequestParam(value = "sign") String sign,
                                            @RequestParam(value = "timeEnd") String timeEnd) throws Exception {
        log.warn("支付回调订单Id：" + id + "  resultCode:" + resultCode + "   shopId:" + shopId + "  money:" + money + "  type:" + type + "  outTradeNo:" + outTradeNo + "  orderId:" + orderId + "  sign:" + sign + "  timeEnd:" + timeEnd);
        return shopOrderService.payNotify(id, resultCode, money, type, sign, timeEnd,outTradeNo);
    }

    @ApiOperation(value = "订单支付")
    @GetMapping("payOrder")
    public ResponseEntity<OrderResponse> payOrder(@RequestParam(value = "orderId", required = false) String orderId, HttpServletRequest request) {
        return shopOrderService.payOrder(orderId, request);
    }

   /* @PostMapping("testPay")
    public ResponseEntity testPay(@RequestBody ShopPayOrderParam payOrderParam) {
        return shopOrderService.testPay(payOrderParam);
    }*/

    @ApiOperation(value = "立即下单")
    @PostMapping("buy")
    public ResponseEntity buy(@RequestBody BuyParam buyParam){
        return shopOrderService.buy(buyParam);
    }

    @ApiOperation(value = "立即下单支付")
    @PostMapping("buyPay")
    public ResponseEntity modifyAddress(@RequestBody ModifyAddressParam modifyAddressParam){
        return shopOrderService.modifyAddress(modifyAddressParam);
    }

    @ApiOperation(value = "确认收货")
    @GetMapping("endOrder")
    public ResponseEntity endOrder(@ApiParam(value = "订单ID", name = "orderId") @RequestParam("orderId") String orderId){
        return shopOrderService.endOrder(orderId);
    }

    @ApiOperation(value = "已支付未发货情况修改收货地址")
    @PostMapping("orderModifyAddress")
    public ResponseEntity orderModifyAddress(@RequestBody ModifyAddressParam modifyAddressParam){
        return shopOrderService.orderModifyAddress(modifyAddressParam);
    }

    @ApiOperation(value = "订单修改到账的虚拟账号")
    @PostMapping("orderModifyVirtualAccount")
    public ResponseEntity orderModifyVirtualAccount(@RequestBody ModifyVirtualAccountParam modifyVirtualAccountParam){
        return shopOrderService.orderModifyVirtualAccount(modifyVirtualAccountParam);
    }

    @ApiOperation(value = "提交订单退款")
    @PostMapping("submitoOrderRefund")
    public ResponseEntity submitoOrderRefund(@RequestBody SubmitoOrderRefundParam submitoOrderRefundParam){
        return shopOrderService.submitoOrderRefund(submitoOrderRefundParam);
    }

    @ApiOperation("第三方支付回调接口")
    @RequestMapping(value="/userPaymentNotifyByPayBOB",method= RequestMethod.POST)
    public ResponseEntity<String> userPaymentNotifyByPayBOB(HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(shopOrderService.userPaymentNotifyByPayBOB(request));
    }
    @PostMapping("payBob")
    public ResponseEntity<Map<String,String>> userRechargeByPayBoB(@RequestBody ModifyAddressParam modifyAddressParam,HttpServletRequest request){
        return  ResponseEntity.ok(shopOrderService.payOrderByBillplz(request,modifyAddressParam));
    }

    @ApiOperation("二维码支付")
    @GetMapping(value="/userPayCode")
    public ResponseEntity<String> userPayCode(@LoginUser Long userCode, @RequestParam("urlText") String urlText) throws Exception {
        return ResponseEntity.ok(shopOrderService.userPayCode(userCode,urlText));
    }

    @ApiOperation(value = "订单再次支付")
    @GetMapping("payOrderAgain")
    public ResponseEntity<Map<String,String>> payOrderAgain(@RequestParam(value = "orderId", required = false) String orderId, HttpServletRequest request){
        return ResponseEntity.ok(shopOrderService.payOrderAgainByBillplz(orderId, request));
    }


}
