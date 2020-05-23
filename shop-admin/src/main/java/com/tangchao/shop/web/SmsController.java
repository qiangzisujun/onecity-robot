package com.tangchao.shop.web;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.pojo.SmsInterface;
import com.tangchao.shop.pojo.SmsTemplate;
import com.tangchao.shop.pojo.SmsType;
import com.tangchao.shop.service.SendSmsMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 13:51
 */
@RequestMapping("/api/sms")
@RestController
@Api(value = "短信设置模块", tags = {"短信设置模块"})
public class SmsController {

    @Autowired
    private SendSmsMessageService smsMessageService;

    @ApiOperation("短信设置--短信类型-列表")
    @GetMapping("/getSmsTypeList")
    public ResponseEntity<PageInfo> getSmsTypeList(@LoginUser Long userId){
        return ResponseEntity.ok(smsMessageService.getCmsInfo(userId));
    }

    @ApiOperation("短信设置--短信类型-编辑")
    @PostMapping("/updateSmsTypeList")
    public ResponseEntity<Void> updateSmsTypeList(@LoginUser Long userId, @RequestBody SmsType type){
        smsMessageService.updateSmsTypeList(userId,type);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信类型-删除")
    @PostMapping("/deleteSmsTypeById")
    public ResponseEntity<Void> deleteSmsTypeById(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        smsMessageService.deleteSmsTypeById(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信类型-新增")
    @PostMapping("/insertSmsType")
    public ResponseEntity<Void> insertSmsType(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        smsMessageService.insertSmsType(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("短信设置--短信模板-列表")
    @GetMapping("/getSmsTemplateList")
    public ResponseEntity<PageInfo> getSmsTemplateList(@LoginUser Long userId){
        return ResponseEntity.ok(smsMessageService.getSmsTemplateList(userId));
    }

    @ApiOperation("短信设置--短信模板-编辑")
    @PostMapping("/updateSmsTemplate")
    public ResponseEntity<Void> updateSmsTemplate(@LoginUser Long userId, @RequestBody SmsTemplate template){
        smsMessageService.updateSmsTemplate(userId,template);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信模板-删除")
    @PostMapping("/deleteSmsTemplate")
    public ResponseEntity<Void> deleteSmsTemplate(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        smsMessageService.deleteSmsTemplate(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信模板-新增")
    @PostMapping("/insertSmsTemplate")
    public ResponseEntity<Void> insertSmsTemplate(@LoginUser Long userId,@RequestBody SmsTemplate template){
        smsMessageService.insertSmsTemplate(userId,template);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信接口-列表")
    @GetMapping("/getSmsInterfaceList")
    public ResponseEntity<PageInfo> getSmsInterfaceList(@LoginUser Long userId){
        return ResponseEntity.ok(smsMessageService.getSmsInterfaceList(userId));
    }

    @ApiOperation("短信设置--短信接口-编辑")
    @PostMapping("/updateSmsInterfaceList")
    public ResponseEntity<Void> updateSmsInterfaceList(@LoginUser Long userId,@RequestBody SmsInterface smsInterface){
        smsMessageService.updateSmsInterfaceList(userId,smsInterface);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信接口-删除")
    @PostMapping("/deleteSmsInterfaceList")
    public ResponseEntity<Void> deleteSmsInterfaceList(@LoginUser Long userId,@RequestBody Map<String,Object> data){
        smsMessageService.deleteSmsInterfaceList(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("短信设置--短信接口-新增")
    @PostMapping("/insertSmsInterfaceList")
    public ResponseEntity<Void> insertSmsInterfaceList(@LoginUser Long userId,@RequestBody SmsInterface smsInterface){
        smsMessageService.insertSmsInterfaceList(userId,smsInterface);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
