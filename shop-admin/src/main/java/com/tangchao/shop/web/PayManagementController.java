package com.tangchao.shop.web;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.pojo.PayCustomer;
import com.tangchao.shop.pojo.PaymentCode;
import com.tangchao.shop.service.PayManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/11 13:46
 */
@Api(value = "支付管理", tags = {"支付管理"})
@RequestMapping("/api/pay/")
@RestController
public class PayManagementController {

    @Autowired
    private PayManagementService payManagementService;

    @ApiOperation("支付客服管理--列表")
    @GetMapping("getPayCustomerService")
    public ResponseEntity<PageInfo> getPayCustomerService(@LoginUser Long userId,
                                                          @ApiParam(value = "页数",name = "pageNo") @RequestParam(value = "pageNo") Integer pageNo,
                                                          @ApiParam(value = "页数大小",name = "pageSize") @RequestParam(value = "pageSize") Integer pageSize){
        return ResponseEntity.ok(payManagementService.payCustomerService(userId,pageNo,pageSize));
    }

    @ApiOperation("支付客服管理--编辑")
    @PostMapping("updatePayCustomerService")
    public ResponseEntity<Void> updatePayCustomerService(@LoginUser Long userId, @RequestBody PayCustomer payCustomer){
        payManagementService.updatePayCustomerService(userId,payCustomer);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("支付客服管理--删除")
    @PostMapping("deletePayCustomerService")
    public ResponseEntity<Void> deletePayCustomerService(@LoginUser Long userId,@ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        payManagementService.deletePayCustomerService(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("支付客服管理--新增")
    @PostMapping("insertPayCustomerService")
    public ResponseEntity<Void> insertPayCustomerService(@LoginUser Long userId,@RequestBody PayCustomer payCustomer){
        payManagementService.insertPayCustomerService(userId,payCustomer);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("用户调用记录--列表")
    @GetMapping("getPayTransferRecordList")
    public ResponseEntity<PageInfo> getPayTransferRecordList(@LoginUser Long userId,
                                                         @ApiParam(value = "会员手机号",name = "mobile") @RequestParam(value = "mobile",required = false) String mobile,
                                                         @ApiParam(value = "微信昵称",name = "weChatNickName") @RequestParam(value = "weChatNickName",required = false) String weChatNickName,
                                                         @ApiParam(value = "调取时间-开始时间",name = "startDate") @RequestParam(value = "startDate",required = false) String startDate,
                                                         @ApiParam(value = "调取时间-结束时间",name = "endDate") @RequestParam(value = "endDate",required = false) String endDate,
                                                         @ApiParam(value = "操作状态(0:未充值，1已充值)",name = "typeId") @RequestParam(value = "typeId",required = false) Integer typeId,
                                                         @ApiParam(value = "页数",name = "pageNo") @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                                         @ApiParam(value = "页数大小",name = "pageSize") @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){

        return ResponseEntity.ok(payManagementService.getPayTransferRecordList(userId,mobile,weChatNickName,startDate,endDate,typeId,pageNo,pageSize));
    }

    @ApiOperation("支付客服管理--点击充值")
    @PostMapping("updatePaymentRecordStatus")
    public ResponseEntity<Void> updatePaymentRecordStatus(@LoginUser Long userId, @ApiParam(value = "用户标识",name = "userCode") @RequestBody Map<String,Object> data, HttpServletRequest request){
        payManagementService.updatePaymentRecordStatus(userId,data,request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("收款码管理--列表")
    @GetMapping("getPaymentCodeList")
    public ResponseEntity<PageInfo> getPaymentCodeList(@LoginUser Long userId,
                                                        @ApiParam(value = "页数",name = "pageNo") @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                                        @ApiParam(value = "页数大小",name = "pageSize") @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){

        return ResponseEntity.ok(payManagementService.getPaymentCodeList(userId,pageNo,pageSize));
    }

    @ApiOperation("收款码管理--编辑")
    @PostMapping("updatePaymentCode")
    public ResponseEntity<Void> updatePaymentCode(@LoginUser Long userId,@RequestBody PaymentCode paymentCode){
        payManagementService.updatePaymentCode(userId,paymentCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("收款码管理--删除")
    @PostMapping("deletePaymentCode")
    public ResponseEntity<Void> deletePaymentCode(@LoginUser Long userId,@ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        payManagementService.deletePaymentCode(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("收款码管理--新增")
    @PostMapping("insertPaymentCode")
    public ResponseEntity<Void> insertPaymentCode(@LoginUser Long userId,@RequestBody PaymentCode paymentCode){
        payManagementService.insertPaymentCode(userId,paymentCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
