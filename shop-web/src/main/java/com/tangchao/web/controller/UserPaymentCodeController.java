package com.tangchao.web.controller;

import com.tangchao.shop.params.UpdateUserPaymentCodeParam;
import com.tangchao.shop.params.UserPaymentCodeParam;
import com.tangchao.user.service.UserPaymentCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Class UserPaymentCodeController
 * @Description TODO
 * @Author Aquan
 * @Date 2020/3/27 11:55
 * @Version 1.0
 **/
@RequestMapping("/userPaymentCode")
@RestController
@Api(value = "商城用户绑定收款码模块", tags = {"商城用户绑定收款码模块"})
public class UserPaymentCodeController {

    @Resource
    private UserPaymentCodeService userPaymentCodeService;

    @ApiOperation(value = "商城用户收款码列表")
    @GetMapping("getUserPaymentCode")
    public ResponseEntity getUserPaymentCode() {
        return userPaymentCodeService.getUserPaymentCode();
    }

    @ApiOperation(value = "绑定收款码")
    @PostMapping("bind")
    public ResponseEntity bind(@RequestBody UserPaymentCodeParam userPaymentCodeParam){
        return userPaymentCodeService.bind(userPaymentCodeParam);
    }

    @ApiOperation(value = "根据Id查询收款码详情")
    @GetMapping("getUserPaymentCodeById")
    public ResponseEntity getUserPaymentCodeById(@PathVariable("id") String id) {
        return userPaymentCodeService.getUserPaymentCodeById(id);
    }

    @ApiOperation(value = "更新收款码")
    @PostMapping("updateBind")
    public ResponseEntity updateBind(@RequestBody UpdateUserPaymentCodeParam updateUserPaymentCodeParam){
        return userPaymentCodeService.updateBind(updateUserPaymentCodeParam);
    }

}
