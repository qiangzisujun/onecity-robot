package com.tangchao.shop.web;

import com.github.pagehelper.PageInfo;
import com.tangchao.common.enums.ExceptionEnum;
import com.tangchao.common.exception.CustomerException;
import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.dto.adminDTO.CustomerDTO;
import com.tangchao.shop.pojo.*;
import com.tangchao.shop.service.GoodsEvaluationShowService;
import com.tangchao.shop.service.OrderGoodsService;
import com.tangchao.shop.service.TradeOrderService;
import com.tangchao.shop.vo.CustomerVO;
import com.tangchao.shop.vo.adminVo.CustomerAdminVO;
import com.tangchao.user.service.CustomerService;
import com.tangchao.user.service.CustomerWithdrawRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/30 21:27
 */
@RequestMapping(value = "/user/mail/account")
@RestController
@Api(value = "会员管理模块",tags = "会员管理模块")
public class MallAccountManagementController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderGoodsService orderGoodsService;

    @Autowired
    private GoodsEvaluationShowService goodsEvaluationShowService;

    @Autowired
    private CustomerWithdrawRecordService customerWithdrawRecordService;

    @Autowired
    private TradeOrderService tradeOrderService;

    @ApiOperation("会员列表")
    @PostMapping("/management")
    public ResponseEntity<PageInfo<com.tangchao.shop.vo.adminVo.CustomerVO>> selectCustomerList(@LoginUser Long userId, @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.selectCustomerList(userId,customerDTO));
    }

    @ApiOperation("会员数量统计")
    @PostMapping("/getUserStatistics")
    public ResponseEntity<Map<String,Object>> getUserStatistics(@LoginUser Long userId) {
        return ResponseEntity.ok(customerService.getUserStatistics(userId));
    }

    @ApiOperation("会员积分明细")
    @GetMapping("/getCustomerScoreDetail")
    public ResponseEntity<Map<String,Object>> getCustomerScoreDetail(@LoginUser Long userId,
                                                                                  @ApiParam(value = "用户标识",name = "userCode") @RequestParam("userCode") Long userCode,
                                                                                  @ApiParam(value = "页数",name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                                                  @ApiParam(value = "页数大小",name = "pageSize") @RequestParam("pageSize") Integer pageSize ){
        return ResponseEntity.ok(customerService.getCustomerScoreDetail(userId,userCode,pageNo,pageSize));
    }

    @ApiOperation("用户地址列表")
    @GetMapping("/getCustomerAddress")
    public ResponseEntity<List<CustomerAddress>> getCustomerAddress(@LoginUser Long userId,
                                                                    @ApiParam(value = "用户标识",name = "userCode") @RequestParam("userCode") Long userCode){
        return ResponseEntity.ok(customerService.getCustomerAddress(userId,userCode));
    }

    @ApiOperation("重置密码")
    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@LoginUser Long userId,
                                              @ApiParam(value = "用户标识",name = "userCode") @RequestBody  Map<String, Object> data){

        Long userCode=Long.valueOf(data.get("userCode").toString());
        customerService.resetPassword(userId,userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("拉黑")
    @PostMapping("/insertBlackList")
    public ResponseEntity<Void> insertBlackList(@LoginUser Long userId,@RequestBody  Map<String, Object> data){

        Long userCode=Long.valueOf(data.get("userCode").toString());
        Integer status=Integer.valueOf(data.get("status").toString());
        customerService.insertBlackList(userId,userCode,status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("删除用户信息")
    @PostMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser(@LoginUser Long userId,
                                           @ApiParam(value = "用户Id",name = "ids") @RequestBody Map<String, Object> ids){
        List<String> list = (List<String>) ids.get("ids");
        customerService.deleteUser(userId,list);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("修改用户信息")
    @PostMapping("/updateCustomer")
    public ResponseEntity<Void> updateCustomer(@LoginUser Long userId,@RequestBody CustomerAdminVO customerVO){
        customerService.updateCustomer(userId,customerVO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("封禁用户")
    @PostMapping("/bannedCustomer")
    public ResponseEntity<Void> bannedCustomer(@LoginUser Long userId,@RequestBody  Map<String, Object> data){

        Long userCode=Long.valueOf(data.get("userCode").toString());
        Integer status=Integer.valueOf(data.get("status").toString());
        customerService.bannedCustomer(userId,userCode,status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("导入机器人")
    @PostMapping("/importRobot")
    public ResponseEntity<Void> importRobot(@LoginUser Long userId, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        customerService.importRobot(userId,file,request,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("新增账号")
    @PostMapping("/addCustomer")
    public ResponseEntity<Void> addCustomer(@LoginUser Long userId,@RequestBody CustomerAdminVO customerAdminVO, HttpServletRequest request) throws IOException {
        customerService.addCustomer(userId,request,customerAdminVO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("会员充值管理--会员充值-用户查询")
    @GetMapping("/selectCustomerInfoByPhone")
    public ResponseEntity<Customer> selectCustomerInfoByPhone(@LoginUser Long userId,
                                                              @ApiParam(value = "会员名称或手机号码",name = "mobile") @RequestParam(value = "mobile") String mobile,
                                                              @ApiParam(value = "类型(1:会员，2代理)",name = "type") @RequestParam(value = "type") Integer type) {
        if (userId == null){
            throw new CustomerException(ExceptionEnum.USER_NOT_AUTHORIZED);
        }
        return ResponseEntity.ok(customerService.selectCustomerInfoByPhone(mobile,type));
    }

    @ApiOperation("会员充值管理--会员充值")
    @PostMapping("/customerRechargeByPhone")
    public ResponseEntity<Void> customerRechargeByPhone(@LoginUser Long userId,@RequestBody Map<String,Object> data,HttpServletRequest request) {
        customerService.customerRechargeByPhone(userId,data,request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @ApiOperation("会员充值管理--会员充值统计")
    @PostMapping("/sumCustomerRechargeByPhone")
    public ResponseEntity<Map<String,Object>> sumCustomerRechargeByPhone(@LoginUser Long userId,@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.sumCustomerRechargeRecordTotal(userId,customerDTO));
    }

    @ApiOperation("会员充值管理--会员充值/消费记录")
    @PostMapping("/customerExpensesRecord")
    public ResponseEntity<Map<String,Object>> customerExpensesRecord(@LoginUser Long userId,@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.customerExpensesRecord(userId,customerDTO));
    }

    @ApiOperation("会员充值管理--代理充值")
    @PostMapping("/proxyRechargeByPhone")
    public ResponseEntity<Void> proxyRechargeByPhone(@LoginUser Long userId,@RequestBody Map<String,Object> data) {
        customerService.proxyRechargeByPhone(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("会员充值管理--代理充值统计")
    @PostMapping("/sumProxyExpensesRecord")
    public ResponseEntity<Map<String,Object>> sumProxyExpensesRecord(@LoginUser Long userId,@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.sumProxyExpensesRecord(userId,customerDTO));
    }

    @ApiOperation("会员充值管理--代理充值/消费记录")
    @PostMapping("/getProxyExpensesRecord")
    public ResponseEntity<Map<String,Object>> getProxyExpensesRecord(@LoginUser Long userId,@RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(customerService.proxyExpensesRecord(userId,customerDTO));
    }


    @ApiOperation("会员晒单管理--会员晒单列表")
    @PostMapping("/getCustomerEvaluationShow")
    public ResponseEntity<PageInfo<CustomerEvaluationShow>> getCustomerEvaluationShow(@LoginUser Long userId, @RequestBody CustomerDTO customerDTO) {
        return ResponseEntity.ok(orderGoodsService.getCustomerEvaluationShow(userId,customerDTO));
    }


    @ApiOperation("会员晒单管理--审核操作")
    @PostMapping("/checkEvaluationShow")
    public ResponseEntity<Void> checkEvaluationShow(@LoginUser Long userId, @RequestBody Map<String,Object> data) {
        goodsEvaluationShowService.getCustomerEvaluationShow(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("会员资金提现管理--列表")
    @PostMapping("/getCustomerEmployWithdrawRecord")
    public ResponseEntity<PageInfo> getCustomerEmployWithdrawRecord(@LoginUser Long userId, @RequestBody CustomerDTO customerDTO) throws ParseException {
        return ResponseEntity.ok(customerWithdrawRecordService.getCustomerEmployWithdrawRecord(userId,customerDTO));
    }

    @ApiOperation("会员资金提现管理--审核操作")
    @PostMapping("/checkEmployWithdraw")
    public ResponseEntity<Void> checkEmployWithdraw(@LoginUser Long userId, @RequestBody Map<String,Object> data) {
        customerWithdrawRecordService.checkEmployWithdraw(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("会员资金提现管理--手动提现")
    @PostMapping("/updateWithdrawAccountsStatus")
    public ResponseEntity<Void> updateWithdrawAccountsStatus(@LoginUser Long userId, @RequestBody Map<String,Object> data){
        customerWithdrawRecordService.updateWithdrawAccountsStatus(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("晒单评论管理--列表")
    @PostMapping("/getCustomerCommentList")
    public ResponseEntity<PageInfo<Map<String,Object>>> getCustomerCommentList(@LoginUser Long userId, @RequestBody CustomerDTO customerDTO){
        return ResponseEntity.ok(customerWithdrawRecordService.getCustomerCommentList(userId,customerDTO));
    }

    @ApiOperation("晒单评论管理--批量已读")
    @PostMapping("/commentBatchSee")
    public ResponseEntity<PageInfo<Map<String,Object>>> commentBatchSee(@LoginUser Long userId, @RequestBody Map<String,Object> data){
        goodsEvaluationShowService.commentBatchSee(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("晒单评论管理--批量删除")
    @PostMapping("/updateEvalDoBatchDeleteByIds")
    public ResponseEntity<PageInfo<Map<String,Object>>> updateEvalDoBatchDeleteByIds(@LoginUser Long userId, @RequestBody Map<String,Object> data){
        goodsEvaluationShowService.updateEvalDoBatchDeleteByIds(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("发货单模板管理--信息")
    @GetMapping("/orderShipTemplate")
    public ResponseEntity<ExpressForm> orderShipTemplate(@LoginUser Long userId){
        return ResponseEntity.ok(tradeOrderService.orderShipTemplate(userId));
    }


    @ApiOperation("发货单模板管理--信息修改保存")
    @PostMapping("/updateExpressFormTemplate")
    public ResponseEntity<Void> updateExpressFormTemplate(@LoginUser Long userId,@RequestBody ExpressForm expressForm){
        tradeOrderService.updateExpressFormTemplate(userId,expressForm);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("会员佣金提现管理--列表")
    @PostMapping("/getCustomerWithdrawList")
    public ResponseEntity<Map<String,Object>> getCustomerWithdrawList(@LoginUser Long userId,@RequestBody CustomerDTO customerDTO){
        return ResponseEntity.ok(customerWithdrawRecordService.getCustomerWithdrawList(userId,customerDTO));
    }


    @ApiOperation("会员佣金提现管理--审核操作")
    @PostMapping("/checkEmployCommission")
    public ResponseEntity<Void> checkEmployCommission(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        customerWithdrawRecordService.checkEmployCommission(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("会员佣金提现管理--手动提现")
    @PostMapping("/checkCommissionWithdraw")
    public ResponseEntity<Void> checkCommissionWithdraw(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        customerWithdrawRecordService.checkCommissionWithdraw(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("手指南--新增")
    @PostMapping("/insertCustomerGuide")
    public ResponseEntity<Void> insertCustomerGuide(@LoginUser Long userId,@RequestBody Guide data){
        customerService.insertCustomerGuide(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("手指南--列表")
    @PostMapping("/getCustomerGuideList")
    public ResponseEntity<PageInfo> getCustomerGuideList(@LoginUser Long userId){
        return ResponseEntity.ok(customerService.getCustomerGuideList(userId));
    }

    @ApiOperation("手指南--删除")
    @PostMapping("/deleteCustomerGuide")
    public ResponseEntity<PageInfo> deleteCustomerGuide(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        customerService.deleteCustomerGuide(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("手指南--编辑")
    @PostMapping("/updateCustomerGuide")
    public ResponseEntity<Void> updateCustomerGuide(@LoginUser Long userId,@RequestBody Guide data){
        customerService.updateCustomerGuide(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("设置机器人购买时间")
    @PostMapping("/setCustomerRobot")
    public ResponseEntity<Void> setCustomerRobot(@RequestBody Map<String,Object> data){
        customerService.setCustomerRobot(data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("用户佣金明细")
    @GetMapping("/getCommissionListByUserCode")
    public ResponseEntity<Map<String,Object>> getCommissionListByAdmin( @ApiParam(value = "用户标识",name = "userCode") @RequestParam("userCode") Long userCode,
                                                          @ApiParam(value = "页数",name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                          @ApiParam(value = "页数大小",name = "pageSize") @RequestParam("pageSize") Integer pageSize){

        return ResponseEntity.ok(customerService.getCommissionListByUserCode(pageNo,pageSize,userCode));
    }

}
