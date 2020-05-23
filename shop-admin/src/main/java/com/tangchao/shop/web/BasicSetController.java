package com.tangchao.shop.web;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.pojo.SeoConfig;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.pojo.WxPayConf;
import com.tangchao.user.service.CmsConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 9:57
 */
@Api(value = "商城管理模块", tags = {"商城管理模块"})
@RequestMapping("/api/cms/")
@RestController
public class BasicSetController {

    @Autowired
    private CmsConfigService configService;

    @ApiOperation("商城管理--基础设置")
    @GetMapping("/getCmsInfo")
    public ResponseEntity<Map<String,Object>> getCmsInfo(@LoginUser Long userId){
        return ResponseEntity.ok(configService.getCmsInfo(userId));
    }

    @ApiOperation("商城管理--基础设置-保存")
    @PostMapping("/updateCmsInfo")
    public ResponseEntity<Void> updateCmsInfo(@LoginUser Long userId, @RequestBody List<Map<String,Object>> maps){
        configService.updateCmsInfo(userId,maps);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商城管理--基本设置(开发)")
    @GetMapping("/getCmsSetUpInfo")
    public ResponseEntity<PageInfo> getCmsSetUpInfo(@LoginUser Long userId,
                                                    @ApiParam(value = "页数",name = "pageNo") @RequestParam(value = "pageNo") Integer pageNo,
                                                    @ApiParam(value = "页数大小",name = "pageSize") @RequestParam(value = "pageSize") Integer pageSize){
        return ResponseEntity.ok(configService.getCmsSetUpInfo(userId,pageNo,pageSize));
    }

    @ApiOperation("商城管理--基本设置(开发)-编辑")
    @PostMapping("/updateCmsSetUpInfo")
    public ResponseEntity<Void> updateCmsSetUpInfo(@LoginUser Long userId, @RequestBody UserConf conf){
        configService.updateCmsSetUpInfo(userId,conf);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商城管理--基本设置(开发)-新增")
    @PostMapping("/insertCmsSetUpInfo")
    public ResponseEntity<Void> insertCmsSetUpInfo(@LoginUser Long userId, @RequestBody UserConf conf){
        configService.insertCmsSetUpInfo(userId,conf);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商城管理--基本设置(开发)-删除")
    @PostMapping("/deleteCmsSetUpInfo")
    public ResponseEntity<Void> deleteCmsSetUpInfo(@LoginUser Long userId, @ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        configService.deleteCmsSetUpInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商城管理--微信支付管理")
    @GetMapping("/wxPayInfo")
    public ResponseEntity<WxPayConf> wxPayInfo(@LoginUser Long userId){
        return ResponseEntity.ok(configService.wxPayInfo(userId));
    }

    @ApiOperation("商城管理--微信支付管理-修改保存")
    @PostMapping("/updateWxPayInfo")
    public ResponseEntity<Void> updateWxPayInfo(@LoginUser Long userId,@RequestBody WxPayConf conf){
        configService.updateWxPayInfo(userId,conf);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("商城管理--SEO配置-列表")
    @PostMapping("/getSeoConfigList")
    public ResponseEntity<PageInfo> getSeoConfigList(@LoginUser Long userId){
        return ResponseEntity.ok(configService.getSeoConfigList(userId));
    }

}
