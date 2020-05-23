package com.tangchao.shop.web;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.dto.adminDTO.GoodsRobotDTO;
import com.tangchao.shop.pojo.Goods;
import com.tangchao.shop.service.GoodsRobotSetService;
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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/10/30 13:55
 */
@RequestMapping("/api/robotSet")
@RestController
@Api(value = "后台机器人管理模块",tags = "后台机器人管理模块")
public class GoodsRootSetController {


    @Autowired
    private GoodsRobotSetService goodsRobotSetService;


    @ApiOperation(value = "机器人列表")
    @GetMapping("/show")
    public ResponseEntity<PageResult<Map>> sellGoods(
                                            @ApiParam(value = "商品名称", name = "goodsName") @RequestParam(value = "goodsName",required = false) String  goodsName,
                                            @ApiParam(value = "商品编号", name = "goodsNo") @RequestParam(value = "goodsNo",required = false) String goodsNo,
                                            @ApiParam(value = "商品类型", name = "goodsType") @RequestParam(value = "goodsType",required = false) Integer typeId,
                                            @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                            @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize){
        return ResponseEntity.ok(goodsRobotSetService.selectGoodsRootSetList(goodsName,goodsNo,typeId,pageNo,pageSize));
    }

    @ApiOperation(value = "添加机器人")
    @PostMapping("/insertGoodsRootSet")
    public ResponseEntity<Void> insertGoodsRootSet(@LoginUser Long userCode, @RequestBody GoodsRobotDTO goodsRobotDTO){
        goodsRobotSetService.insertGoodsRootSet(userCode,goodsRobotDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改机器人信息")
    @PostMapping("/updateGoodsRootSet")
    public ResponseEntity<Void> updateGoodsRootSet(@LoginUser Long userCode, @RequestBody GoodsRobotDTO goodsRobotDTO){
        goodsRobotSetService.updateGoodsRootSet(userCode,goodsRobotDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "删除机器人")
    @PostMapping("/deleteGoodsRootSet")
    public ResponseEntity<Void> deleteGoodsRootSet(@LoginUser Long userCode,
                                                   @ApiParam(value = "机器人Id",name = "id") @RequestParam("id") Long id){
        goodsRobotSetService.deleteGoodsRootSet(userCode,id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "导入机器人设置")
    @PostMapping("/importRobotSet")
    public ResponseEntity<Void> importRobotSet(@LoginUser Long userCode, MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        goodsRobotSetService.importRobotSet(userCode,file,request,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "导出机器人设置")
    @GetMapping("/exportRobotSet")
    public ResponseEntity<Void> exportRobotSet(@LoginUser Long userCode, HttpServletResponse response) throws UnsupportedEncodingException {
        goodsRobotSetService.exportRobotSet(userCode,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
