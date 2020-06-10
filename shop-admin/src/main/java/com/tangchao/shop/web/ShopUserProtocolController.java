package com.tangchao.shop.web;

import com.tangchao.shop.pojo.UserProtocol;
import com.tangchao.shop.service.UserProtocolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2020/4/2 14:05
 */
@Api(value = "用户协议",tags = "用户协议")
@RestController
@RequestMapping("/UserProtocol/")
public class ShopUserProtocolController {

    @Autowired
    private UserProtocolService userProtocolService;

    @ApiOperation("添加用户协议")
    @PostMapping("add")
    private ResponseEntity<Void> addUserProtocol(@RequestBody UserProtocol userProtocol){
        userProtocolService.insertUserProtocol(userProtocol);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("修改用户协议")
    @PostMapping("update")
    private ResponseEntity<Void> updateUserProtocol(@RequestBody UserProtocol userProtocol){
        userProtocolService.updateUserProtocol(userProtocol);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("删除用户协议")
    @PostMapping("delete")
    private ResponseEntity<Void> deleteUserProtocol(@RequestParam("id") Integer id){
        userProtocolService.deleteUserProtocol(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("获取用户协议")
    @GetMapping("show")
    private ResponseEntity<List<UserProtocol>> getUserProtocol(){
        return ResponseEntity.ok(userProtocolService.getUserProtocol());
    }
}
