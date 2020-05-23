package com.tangchao.shop.web;

import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.dto.adminDTO.ManagerUserDTO;
import com.tangchao.shop.vo.adminVo.ManagerUserVO;
import com.tangchao.user.service.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/28 12:51
 */
@RestController
@Api(value = "元购后台登录/退出模块",tags = "元购后台登录/退出模块")
public class LoginController {

    @Autowired
    private ManagerService managerService;

    @ApiOperation(value = "后台登录")
    @PostMapping("/login")
    public ResponseEntity<ManagerUserVO> login(@RequestBody ManagerUserDTO userDTO, HttpServletRequest request) {
        return ResponseEntity.ok(managerService.managerLogin(userDTO,request));
    }

    /**
     * 验证码生成
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "验证码生成")
    @GetMapping(value = "/captcha/captchaImage")
    public ResponseEntity<String> getKaptchaImage() throws IOException {
        return ResponseEntity.ok(managerService.getKaptchaImage());
    }

    @ApiOperation(value = "后台退出")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@LoginUser Long userCode) {
        managerService.managerLogout(userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
