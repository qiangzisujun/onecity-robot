package com.tangchao.web.controller;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.CustomerDto;
import com.tangchao.shop.dto.CustomerWithdrawDTO;
import com.tangchao.shop.dto.UserDTO;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.*;
import com.tangchao.shop.vo.*;
import com.tangchao.user.service.*;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


@RequestMapping("/api/user/home")
@RestController
@Api(value = "元购用户个人中心", tags = {"元购用户个人中心"})
public class CustomerHomeController {

    @Autowired
    private CustomerWithdrawRecordService withdrawRecordService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private GoodsFavouriteService goodsFavouriteService;

    @Autowired
    private WinningOrderService winningOrderService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private GoodsEvaluationShowService goodsEvaluationShowService;

    @Autowired
    private CmsConfigService cmsConfigService;

    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private CustomerScoreDetailService customerScoreDetailService;

    @Autowired
    private GoodsStageService stageService;

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private CustomerSignService signService;

    @ApiOperation("提现记录")
    @GetMapping(value = "/customerWithdrawRecord")
    public ResponseEntity<PageResult<CustomerWithdrawRecordVO>> customerWithdrawRecord(
            @LoginUser Long userCode,
            @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
            @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize) {

        return ResponseEntity.ok(withdrawRecordService.getCustomerWithdrawRecord(pageNo, pageSize, userCode));
    }

    @ApiOperation("好友列表")
    @GetMapping(value = "/findCustomerFriendList")
    public ResponseEntity<PageResult<CustomerVO>> findCustomerFriendList(
            @LoginUser Long userCode,
            @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
            @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize) {

        return ResponseEntity.ok(customerService.findCustomerFriendList(pageNo, pageSize, userCode));
    }

    @ApiOperation("佣金明细")
    @GetMapping("/CommissionList")
    public ResponseEntity<PageResult<OrderDistribution>> CommissionList(@LoginUser Long userCode,
                                                                        @ApiParam(value = "页数",name ="pageNo") @RequestParam("pageNo") Integer pageNo,
                                                                        @ApiParam(value = "页数大小",name ="pageSize") @RequestParam("pageSize") Integer pageSize){
        return ResponseEntity.ok(customerService.CommissionList(userCode,pageNo,pageSize));
    }

    @ApiOperation("佣金提现申请")
    @RequestMapping(value = "/customerWithdraw", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Void> customerWithdraw(@LoginUser Long userCode, @RequestBody CustomerWithdrawDTO dto) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        dto.setCustomerCode(userCode);
        withdrawRecordService.addCustomerWithdraw(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("佣金提现申请手续费")
    @GetMapping("/withdrawHandlingFee")
    @ResponseBody
    public ResponseEntity<String> withdrawHandlingFee(@LoginUser Long userCode) {
        if (userCode == null) {
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        return ResponseEntity.ok(withdrawRecordService.withdrawHandlingFee());
    }

    @ApiOperation("收藏")
    @GetMapping("/saveGoodsFavourite")
    @ResponseBody
    public ResponseEntity<Void> saveGoodsFavourite(@LoginUser Long userCode, @ApiParam(value = "商品编号", name = "goodsNo") @RequestParam(value = "goodsNo") Long goodsNo) {
        goodsFavouriteService.saveGoodsFavourite(userCode, goodsNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商品详情取消收藏")
    @GetMapping("/deleteGoodsFavouriteByGoosNo")
    @ResponseBody
    public ResponseEntity<Void> deleteGoodsFavouriteByGoosNo(@LoginUser Long userCode, @ApiParam(value = "商品编号", name = "goodsNo") @RequestParam(value = "goodsNo") Long goodsNo) {
        goodsFavouriteService.deleteGoodsFavouriteByGoodsNo(userCode, goodsNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation("商品收藏列表")
    @GetMapping("/goodsFavouriteByGoosNoList")
    @ResponseBody
    public ResponseEntity<PageResult<GoodsFavourite>> goodsFavouriteByGoosNoList(@LoginUser Long userCode,
                                                                                 @ApiParam(value = "页数",name ="pageNo") @RequestParam("pageNo") Integer pageNo,
                                                                                 @ApiParam(value = "页数大小",name ="pageSize") @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(goodsFavouriteService.goodsFavouriteByGoosNoList(userCode,pageNo,pageSize));
    }


    @ApiOperation("用户汇购记录")
    @GetMapping("/buyList")
    public ResponseEntity<PageResult<OrderNoteVO>> buyList(@LoginUser Long userCode,
                                                           @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                           @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize,
                                                           @ApiParam(value = "开奖状态(0:未开奖,1:已开奖)", name = "openWinningStatus") @RequestParam(value = "openWinningStatus") Integer openWinningStatus) {
        return ResponseEntity.ok(goodsFavouriteService.buyList(userCode, pageNo, pageSize, openWinningStatus));
    }

    @ApiOperation("用户获奖商品")
    @GetMapping("/prizeWinning")
    public ResponseEntity<Map<String,Object>> prizeWinning(@LoginUser Long userCode,
                                                                 @ApiParam(value = "页数",name ="pageNo") @RequestParam("pageNo") Integer pageNo,
                                                                 @ApiParam(value = "页数大小",name ="pageSize") @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(winningOrderService.prizeWinning(userCode,pageNo,pageSize));

    }

    @ApiOperation("用户资金明细")
    @GetMapping("/consumeRecord")
    public ResponseEntity<CapitalDetailsVO> consumeRecord(@LoginUser Long userCode) {
        return ResponseEntity.ok(goodsFavouriteService.consumeRecord(userCode));
    }

    @ApiOperation("用户资金明细记录")
    @GetMapping("/consumeRecordList")
    public ResponseEntity<PageResult<CustomerRechargeRecord>> consumeRecordList(@LoginUser Long userCode,
            @ApiParam(value = "页数",name ="pageNo") @RequestParam("pageNo") Integer pageNo,
            @ApiParam(value = "页数大小",name ="pageSize") @RequestParam("pageSize") Integer pageSize,
            @ApiParam(value = "0消费记录,1充值记录,2福分记录",name ="count") @RequestParam("count") Integer count) {
        return ResponseEntity.ok(goodsFavouriteService.consumeRecordList(userCode,pageNo,pageSize,count));
    }

    @ApiOperation("客服咨询")
    @GetMapping("/consultation")
    public ResponseEntity<Map<String,Object>> consultation() {
        return ResponseEntity.ok(cmsConfigService.consultation());
    }

    @ApiOperation("账号设置")
    @GetMapping("/updateAccountNumber")
    @ResponseBody
    public ResponseEntity<Void> updateAccountNumber(@LoginUser Long userCode,
                                                    @ApiParam(value = "用户头像",name ="portrait")@RequestParam(value = "portrait",required = false)String portrait ,
                                                    @ApiParam(value = "用户昵称",name ="realname") @RequestParam(value = "realname",required = false) String realname,
                                                    @ApiParam(value = "是否公开拼团记录{0：公开，1：隐藏}",name ="isCollageRecord") @RequestParam(value = "isCollageRecord",required = false)Integer isCollageRecord,
                                                    @ApiParam(value = "是否公开获得的商品{0：公开，1：隐藏}",name ="isObtainGoods") @RequestParam(value = "isObtainGoods",required = false)Integer isObtainGoods) {
        customerService.updateAccountNumber(userCode,portrait,realname,isCollageRecord,isObtainGoods);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation("修改密码")
    @PostMapping("/updatePass")
    public ResponseEntity<Void> updatePass(@LoginUser Long userCode,
                                           @ApiParam(value = "用户新密码",name ="pwd") @RequestParam(value = "pwd")String pwd,
                                           @ApiParam(value = "用户旧密码",name ="oldPass") @RequestParam(value = "oldPass")String oldPass) {
        customerService.updatePass(userCode,pwd,oldPass);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商品详情所有参与记录-用户个人信息")
    @GetMapping("/getCustomerInfo")
    public ResponseEntity<CustomerVO> getCustomerInfo(
            @ApiParam(value = "用户标识", name = "userCode") @RequestParam("userCode") Long userCode) {
        return ResponseEntity.ok(customerService.getCustomerInfo(userCode));
    }

    @ApiOperation("用户购买记录")
    @GetMapping("/getCustomerByList")
    public ResponseEntity<PageResult<UserByRecordVO>> getCustomerByList(
            @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
            @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize,
            @ApiParam(value = "用户标识", name = "userCode") @RequestParam("userCode") Long userCode) {
        return ResponseEntity.ok(orderGoodsService.selectBuyRecordList(pageNo, pageSize, userCode));
    }

    @ApiOperation("用户获得商品")
    @GetMapping("/selectUserGetGoods")
    public ResponseEntity<PageResult<UserByRecordVO>> selectUserGetGoods(
            @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
            @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize,
            @ApiParam(value = "用户标识", name = "userCode") @RequestParam("userCode") Long userCode) {
        return ResponseEntity.ok(orderGoodsService.selectUserObtainGoodsList(pageNo, pageSize, userCode));
    }

    @ApiOperation("历史晒单")
    @GetMapping("/getUserHistoryShowOrder")
    public ResponseEntity<PageResult<OrderShowVO>> getUserHistoryShowOrder(
            @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
            @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize,
            @ApiParam(value = "用户标识", name = "userCode") @RequestParam("userCode") Long userCode) {
        return ResponseEntity.ok(goodsEvaluationShowService.selectOrderShowList(pageNo, pageSize, userCode));
    }

    @ApiOperation(value = "元购个人信息")
    @PostMapping("/getUserInfo")
    public ResponseEntity<CustomerVO> getUserInfo(@LoginUser Long userCode) {
        return ResponseEntity.ok(customerService.getCustomerInfoShop(userCode));
    }

    /**
     * 获取用户微信登录信息
     * @return
     */
    @ApiOperation(value = "元购充值")
    @GetMapping("/getPaymentCode")
    public ResponseEntity<Void> getPaymentCode(String code, String state, HttpServletRequest request,HttpServletResponse response) throws IOException {
        customerService.getPaymentCode(code,state,request,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation(value = "收款码充值")
    @PostMapping("/paymentCodeRecord")
    public ResponseEntity<String> paymentCodePay(@LoginUser Long userCode,@RequestBody Map<String,Object> data) {


        return ResponseEntity.ok(customerService.paymentCodePay(userCode,data));
    }

    @ApiOperation(value = "线下充值")
    @PostMapping("/OfflinePay")
    public ResponseEntity<String> OfflinePay(@LoginUser Long userCode) {
        return ResponseEntity.ok(customerService.paymentCodeImg(userCode));
    }

    @ApiOperation("忘记密码")
    @PostMapping("/forgetPass")
    public ResponseEntity<Void> forgetPass(@ApiParam(value = "手机号码",name ="phone") @RequestParam(value = "phone")String phone,
                                           @ApiParam(value = "用户新密码",name ="pwd") @RequestParam(value = "pwd")String pwd,
                                           @ApiParam(value = "验证码",name ="code") @RequestParam(value = "code")String code) {
        customerService.forgetPass(pwd,code,phone);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("网页授权登录")
    @GetMapping("/getWXCode")
    public ResponseEntity<String> getWXCode(HttpServletRequest request) throws UnsupportedEncodingException{
        return ResponseEntity.ok(customerService.getWXCode(request));
    }

    @ApiOperation("收款码金额")
    @GetMapping("/getCollectionCodeList")
    public  ResponseEntity<List<String>> getCollectionCodeList(@LoginUser Long userId) {
        return ResponseEntity.ok(customerService.getCollectionCodeList(userId));
    }

    @ApiOperation("中奖订单-完善收货地址")
    @PostMapping("/setupAddress")
    public  ResponseEntity<Void> setupAddress(@LoginUser Long userId,@RequestBody Map<String,Object> data) {
        customerService.setupAddress(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("中奖订单-出售转让")
    @PostMapping("/sellTransfer")
    public  ResponseEntity<Void> sellTransfer(@LoginUser Long userId,@RequestBody Map<String,Object> data) {
        customerService.sellTransfer(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("代理充值")
    @PostMapping("/proxyRecharge")
    public  ResponseEntity<Void> proxyRecharge(@LoginUser Long userId,@RequestBody Map<String,Object> data) {
        customerService.proxyRecharge(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("代理充值--用户查询")
    @GetMapping("/selectCustomerInfoByPhone")
    public ResponseEntity<Customer> selectCustomerInfoByPhone(@LoginUser Long userId,
                                                              @ApiParam(value = "会员名称或手机号码",name = "mobile") @RequestParam(value = "mobile") String mobile) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        return ResponseEntity.ok(customerService.selectCustomerInfoByPhone(mobile,1));
    }

    @ApiOperation("代理充值--充值记录")
    @PostMapping("/selectProxyRechargeList")
    public ResponseEntity<Map<String,Object>> selectProxyRechargeList(@LoginUser Long userId,@RequestBody CustomerDto customerDto) {
        customerDto.setUserCode(userId);
        return ResponseEntity.ok(customerService.selectProxyRechargeList(customerDto));
    }

    @ApiOperation("代理充值--秒款订单")
    @PostMapping("/selectProxyOrderList")
    public ResponseEntity<Map<String,Object>> selectProxyOrderList(@LoginUser Long userId,@RequestBody CustomerDto customerDto) {
        customerDto.setUserCode(userId);
        return ResponseEntity.ok(customerService.selectProxyOrderList(customerDto));
    }


    @ApiOperation("代理 秒款订单核销")
    @GetMapping("/batchCheck")
    public ResponseEntity<Void> batchCheck(@LoginUser Long userId,
                                           @ApiParam(value = "核销码",name = "checkCode") @RequestParam("checkCode") String checkCode,
                                           @ApiParam(value = "id",name = "id") @RequestParam("id") Long id) {
        tradeOrderService.proxyCompleteCode(userId,checkCode,id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("用户福分消费")
    @GetMapping("/scoreDetail/list")
    public ResponseEntity<PageInfo> consumeRecordList(@LoginUser Long userCode,
                                                      @ApiParam(value = "页数",name ="pageNo") @RequestParam("pageNo") Integer pageNo,
                                                      @ApiParam(value = "页数大小",name ="pageSize") @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok(customerScoreDetailService.findCustomerScoreDetailByCustomerCodePage(userCode,pageNo,pageSize));
    }

    @ApiOperation("账户充值")
    @PostMapping("/customerEmployRechargeUserMoney")
    public ResponseEntity<PageInfo> customerEmployRechargeUserMoney(@LoginUser Long userCode,@RequestBody Map<String,Object> data) {
        customerScoreDetailService.customerEmployRechargeUserMoney(userCode,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("为你推荐")
    @GetMapping("/getRecommendList")
    public ResponseEntity<PageInfo> getRecommendList(@LoginUser Long userId){
        return ResponseEntity.ok(stageService.getRecommendList(userId));
    }

    @ApiOperation("个人中心-统计")
    @GetMapping("/getUserStatistics")
    public ResponseEntity<Map<String,Object>> getUserStatistics(@LoginUser Long userId){
        return ResponseEntity.ok(orderGoodsService.getUserStatistics(userId));
    }

    @ApiOperation("获奖商品-物流信息")
    @GetMapping("/getLogisticsInfo")
    public ResponseEntity<WinningOrder> getLogisticsInfo(@LoginUser Long userCode,@RequestParam("goodsNo") String goodsNo,@RequestParam("stageId") Integer stageId){
        return ResponseEntity.ok(orderGoodsService.getLogisticsInfo(userCode,goodsNo,stageId));
    }

    @ApiOperation("获奖商品-查看核销码")
    @GetMapping("/getCheckCode")
    public ResponseEntity<String> getCheckCode(@LoginUser Long userCode,@RequestParam("winOrderId") String winOrderId){
        return ResponseEntity.ok(winningOrderService.getCheckCode(userCode,winOrderId));
    }

    @ApiOperation("获奖商品-确认收货")
    @GetMapping("/delivery")
    public ResponseEntity<Map<String,Object>> delivery(@LoginUser Long userCode,@RequestParam("orderNo") String orderNo){
        winningOrderService.delivery(userCode,orderNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("显示幸运号")
    @GetMapping("/selectBuyDetail")
    public ResponseEntity<List<OrderNoteVO>> selectBuyDetail(@ApiParam(value = "用户标识",name = "userCode") @RequestParam(value = "userCode") Long userCode,
                                                             @ApiParam(value = "商品编号",name = "goodsNo") @RequestParam(value = "goodsNo") String goodsNo,
                                                             @ApiParam(value = "商品期数",name = "goodsStage") @RequestParam(value = "goodsStage") Integer goodsStage,
                                                             @ApiParam(value = "状态:0进行中,1已揭晓",name = "openWinningStatus") @RequestParam(value = "openWinningStatus") Integer openWinningStatus) {

        return ResponseEntity.ok(lotteryService.selectBuyDetail(userCode,null,goodsNo,goodsStage));
    }

    @ApiOperation("用户签到")
    @GetMapping("/addCustomerSignIn")
    public ResponseEntity<Map<String,Object>> addCustomerSignIn(){
        return ResponseEntity.ok(signService.insertCustomerSignRecord());
    }

    @ApiOperation("用户签到")
    @GetMapping("/isCustomerSignIn")
    public ResponseEntity<Map<String,Object>> isCustomerSignIn(){
        return ResponseEntity.ok(signService.isCustomerSignIn());
    }
}
