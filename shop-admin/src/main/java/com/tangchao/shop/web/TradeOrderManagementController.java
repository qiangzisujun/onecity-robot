package com.tangchao.shop.web;

import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.dto.adminDTO.OrderDTO;
import com.tangchao.shop.pojo.TradeOrder;
import com.tangchao.shop.service.TradeOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/31 18:12
 */
@RestController
@RequestMapping("/order/trade")
@Api(value = "交易订单管理模块",tags = "交易订单管理模块")
public class TradeOrderManagementController {

    @Autowired
    private TradeOrderService tradeOrderService;


    @ApiOperation("交易订单列表")
    @PostMapping("/management")
    public ResponseEntity<Map<String,Object>> selectOrderTradeList(@LoginUser Long userId, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(tradeOrderService.selectOrderTradeList(userId,orderDTO));
    }

    @ApiOperation("交易订单统计")
    @PostMapping("/countOrderTradeList")
    public ResponseEntity<Map<String,Object>> countOrderTradeList(@LoginUser Long userId, @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(tradeOrderService.countOrderTradeList(userId,orderDTO));
    }


    @ApiOperation("交易订单列表详情")
    @GetMapping("/getOrderDetailInfo")
    public ResponseEntity<TradeOrder> getOrderDetailInfo(@LoginUser Long userId,
                                                         @ApiParam(value = "订单编号",name = "orderNo") @RequestParam(value = "orderNo") String orderNo) {
        return ResponseEntity.ok(tradeOrderService.getOrderDetailInfo(userId,orderNo));
    }

    @ApiOperation("指定用户中奖")
    @PostMapping("/setLuckyUser")
    public ResponseEntity<Void> setLuckyUser(@LoginUser Long userId, @RequestBody Map<String,Object> data) {
        String stageId=data.get("stageId").toString();
        String userCode=data.get("userCode").toString();
        tradeOrderService.setLuckyUser(userId,stageId,userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("取消指定用户中奖")
    @PostMapping("/closeLuckyUser")
    public ResponseEntity<Void> closeLuckyUser(@LoginUser Long userId,@RequestBody Map<String,Object> data) {
        String stageId=data.get("stageId").toString();
        String userCode=data.get("userCode").toString();
        tradeOrderService.closeLuckyUser(userId,stageId,userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("秒款订单列表")
    @PostMapping("/getFastOrderList")
    public ResponseEntity<Map<String,Object>> getFastOrderList(@LoginUser Long userId,@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(tradeOrderService.getFastOrderList(userId,orderDTO));
    }


    @ApiOperation("秒款订单核销")
    @PostMapping("/batchCheck")
    public ResponseEntity<Void> batchCheck(@LoginUser Long userId,@RequestBody Map<String, Object> data) {
        tradeOrderService.autoCompleteCode(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("中奖订单管理--中奖订单列表")
    @PostMapping("/getWinningOrderList")
    public ResponseEntity<Map<String,Object>> getWinningOrderList(@LoginUser Long userId,@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(tradeOrderService.getWinningOrderList(userId,orderDTO));
    }

    @ApiOperation("中奖订单管理--中奖订单统计")
    @PostMapping("/countOrderList")
    public ResponseEntity<Map<String,Object>> countOrderList(@LoginUser Long userId,@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(tradeOrderService.countOrderList(userId,orderDTO));
    }


    @ApiOperation("中奖订单管理--中奖订单详情")
    @GetMapping("/WinningOrderInfo")
    public ResponseEntity<Map<String,Object>> WinningOrderInfo(@LoginUser Long userId,String orderOrder) {
        return ResponseEntity.ok(tradeOrderService.WinningOrderInfo(userId,orderOrder));
    }

    @ApiOperation("中奖订单管理--中奖订单详情-发货")
    @PostMapping("/winningOrderDelivery")
    public ResponseEntity<Void> winningOrderDelivery(@LoginUser Long userId,@RequestBody Map<String,Object> data) {
        tradeOrderService.updateWinningOrderInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("中奖订单管理--导出中奖订单")
    @GetMapping("/exportWinningOrder")
    public ResponseEntity<Void> exportWinningOrder(@LoginUser Long userId, String goodsNo,String orderNo,String userName,String userMobile,String createStartTime,String createEndTime,Integer status,HttpServletResponse response) throws UnsupportedEncodingException {
        tradeOrderService.exportWinningOrder(userId,goodsNo,orderNo,userName,userMobile,createStartTime,createEndTime,status,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
