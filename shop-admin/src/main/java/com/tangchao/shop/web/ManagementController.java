package com.tangchao.shop.web;


import com.github.pagehelper.PageInfo;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.pojo.Program;
import com.tangchao.shop.pojo.Role;
import com.tangchao.shop.vo.adminVo.ManagerUserVO;
import com.tangchao.user.service.ManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/system/")
@RestController
@Api(value = "系统管理模块", tags = {"系统管理模块"})
public class ManagementController {

    @Autowired
    private ManagerService managerService;

    @ApiOperation("用户管理--列表")
    @GetMapping("getManagerList")
    public ResponseEntity<PageInfo> getManagerList(@LoginUser Long userId,
                      @ApiParam(value = "用户名",name = "userName") @RequestParam(value = "userName",required = false) String userName,
                      @ApiParam(value = "角色id",name = "role") @RequestParam(value = "role",required = false) Integer roleId,
                      @ApiParam(value = "页数",name = "pageNo") @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                      @ApiParam(value = "页数大小",name = "pageSize") @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){

        return ResponseEntity.ok(managerService.getManagerList(userId,userName,roleId,pageNo,pageSize));
    }

    @ApiOperation("用户列表-编辑")
    @PostMapping("updateManagerInfo")
    public ResponseEntity<Void> updateManagerInfo(@LoginUser Long userId,@RequestBody ManagerUserVO vo){
        managerService.updateManagerInfo(userId,vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("用户列表-删除")
    @PostMapping("deleteManagerInfo")
    public ResponseEntity<Void> deleteManagerInfo(@LoginUser Long userId,@ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        managerService.deleteManagerInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("用户列表-新增")
    @PostMapping("insertManagerInfo")
    public ResponseEntity<Void> insertManagerInfo(@LoginUser Long userId,@RequestBody ManagerUserVO vo){
        managerService.insertManagerInfo(userId,vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("用户列表-重置密码")
    @PostMapping("managerResetPassword")
    public ResponseEntity<Void> managerResetPassword(@LoginUser Long userId,@ApiParam(value = "id",name = "id")@RequestBody Map<String,Object> data){
        managerService.managerResetPassword(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("角色管理--列表")
    @GetMapping("getRoleList")
    public ResponseEntity<List<Role>> getRoleList(@LoginUser Long userId,
                                                  @ApiParam(value = "角色名称",name = "roleName") @RequestParam(value = "roleName",required = false) String roleName){
        return ResponseEntity.ok(managerService.getRoleList(userId,roleName));
    }

    @ApiOperation("角色管理--编辑")
    @PostMapping("updateRoleInfo")
    public ResponseEntity<Void> updateRoleInfo(@LoginUser Long userId,
                                                     @ApiParam(value = "id",name = "id,roleName") @RequestBody Map<String,Object> data){
        managerService.updateRoleInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("角色管理--删除")
    @PostMapping("deleteRoleInfo")
    public ResponseEntity<Void> deleteRoleInfo(@LoginUser Long userId,
                                                     @ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        managerService.deleteRoleInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("角色管理--新增")
    @PostMapping("insertRoleInfo")
    public ResponseEntity<Void> insertRoleInfo(@LoginUser Long userId,
                                               @ApiParam(value = "角色名称",name = "roleName") @RequestBody Map<String,Object> data){
        managerService.insertRoleInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("系统程序--列表")
    @GetMapping("getProgramList")
    public ResponseEntity<PageInfo> getProgramList(@LoginUser Long userId,
                                               @ApiParam(value = "页数",name = "pageNo") @RequestParam(value = "pageNo",defaultValue = "1") Integer pageNo,
                                               @ApiParam(value = "页数大小",name = "pageSize") @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return ResponseEntity.ok(managerService.getProgramList(userId,pageNo,pageSize));
    }

    @ApiOperation("系统程序--编辑")
    @PostMapping("updateProgramList")
    public ResponseEntity<Void> updateProgramList(@LoginUser Long userId, @RequestBody Program program){
        managerService.updateProgramList(userId,program);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("系统程序--删除")
    @PostMapping("deleteProgramList")
    public ResponseEntity<Void> deleteProgramList(@LoginUser Long userId,@ApiParam(value = "id集合",name = "ids") @RequestBody Map<String,Object> data){
        managerService.deleteProgramList(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("系统程序--新增")
    @PostMapping("insertProgramList")
    public ResponseEntity<Void> insertProgramList(@LoginUser Long userId, @RequestBody Program program){
        managerService.insertProgramList(userId,program);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
