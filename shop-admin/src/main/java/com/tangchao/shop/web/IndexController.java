package com.tangchao.shop.web;


import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.mapper.ManagerMapper;
import com.tangchao.shop.mapper.ProgramMapper;
import com.tangchao.shop.mapper.RoleMapper;
import com.tangchao.shop.pojo.Manager;
import com.tangchao.shop.pojo.Menu;
import com.tangchao.shop.pojo.Program;
import com.tangchao.shop.pojo.Role;
import com.tangchao.shop.service.TradeOrderService;
import com.tangchao.shop.service.UploadService;
import com.tangchao.shop.vo.adminVo.ManagerUserVO;
import com.tangchao.user.service.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/admin")
@RestController
@Api(value = "后台首页模块", tags = {"后台首页模块"})
public class IndexController {

    @Autowired
    private TradeOrderService tradeOrderService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private UploadService uploadService;


    @ApiOperation(value = "订单统计模块")
    @PostMapping("/OrderManagement")
    public ResponseEntity<Map> OrderManagement(){
        return ResponseEntity.ok(tradeOrderService.OrderManagement());
    }


    /**
     * 主页（需要登录）
     *
     */
    @ApiOperation(value = "首页导航栏")
    @GetMapping("/index/nav")
    public ResponseEntity<Map<String,Object>> index(@LoginUser String userName) {
        return ResponseEntity.ok(managerService.index(userName));
    }


    @ApiOperation(value = "后台图片上传")
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }


    @ApiOperation(value = "后台用户详情")
    @PostMapping("/selectUserInfo")
    public ResponseEntity<ManagerUserVO> selectUserInfo(@LoginUser Long userCode) {
        return ResponseEntity.ok(managerService.selectUserInfo(userCode));
    }

}
