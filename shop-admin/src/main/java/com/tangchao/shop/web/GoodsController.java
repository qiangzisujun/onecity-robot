package com.tangchao.shop.web;


import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.pojo.Goods;
import com.tangchao.shop.pojo.GoodsType;
import com.tangchao.shop.service.GoodsService;
import com.tangchao.shop.service.GoodsTypeService;
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

@RequestMapping("/api/goods/")
@RestController
@Api(value = "后台商品模块", tags = {"后台商品模块"})
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsTypeService goodsTypeService;

    @ApiOperation(value = "商品分类列表")
    @GetMapping("/goodsTypeList")
    public ResponseEntity<List<GoodsType>>  goodsTypeList(){
        return ResponseEntity.ok(goodsTypeService.goodsTypeList());
    }

    @ApiOperation(value = "新增商品分类")
    @PostMapping("/addGoodsType")
    public ResponseEntity<Void>  addGoodsType(@LoginUser Long userId,@RequestBody GoodsType type){
        goodsTypeService.addGoodsType(type.getTypeName(),type.getTypePid(),userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改商品分类")
    @PostMapping("/updateGoodsType")
    public ResponseEntity<Void>  updateGoodsType(@RequestBody GoodsType goodsType){
        goodsTypeService.updateGoodsType(goodsType );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改排序")
    @PostMapping("/updateSort")
    public ResponseEntity<Void>  updateSort(@RequestBody Map<String,Object> data){
        Long id1=Long.valueOf(data.get("id1").toString());
        Long id2=Long.valueOf(data.get("id2").toString());
        goodsTypeService.updateSort(id1,id2);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "删除商品分类")
    @GetMapping("/deleteGoodsType")
    public ResponseEntity<Void>  deleteGoodsType(@ApiParam(value = "商品分类Id", name = "id") @RequestParam(value = "id") Long id){
        goodsTypeService.delectGoodsType(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



    @ApiOperation(value = "商品内容管理--商品内容列表")
    @GetMapping("/goodsList")
    public ResponseEntity<PageResult<Goods>>  goodsList( @ApiParam(value = "商品名称", name = "goodsName") @RequestParam(value = "goodsName",required = false) String  goodsName,
                                                         @ApiParam(value = "商品类型", name = "goodsType") @RequestParam(value = "goodsType",required = false) Integer typeId,
                                                         @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                         @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize){
        return ResponseEntity.ok(goodsService.goodsList(pageNo,pageSize,goodsName,typeId));
    }

    @ApiOperation(value = "商品内容管理-添加商品")
    @PostMapping("/addGoods")
    public ResponseEntity<Void>  addGoods(@RequestBody Goods goods){
        goodsService.addGoods(goods);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品内容管理-删除商品")
    @GetMapping("/deleteGoods")
    public ResponseEntity<Void>  deleteGoods(@LoginUser Long userId, @ApiParam(value = "商品编号", name = "goodsNo") @RequestParam(value = "goodsNo") String goodsNo){
        goodsService.deleteGoods(userId,goodsNo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品内容管理-修改商品信息")
    @PostMapping("/updateGoods")
    public ResponseEntity<Void>  updateGoods(@RequestBody Goods goods){
        goodsService.updateGoods(goods);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品内容管理-上下架")
    @PostMapping("/shelfGoods")
    public ResponseEntity<Void>  shelfGoods(@LoginUser Long userCode,@RequestBody Map<String,Object> data){
        Long id=Long.valueOf(data.get("id").toString());
        Integer isSell=Integer.valueOf(data.get("isSell").toString());
        goodsService.shelfGoods(userCode,id,isSell);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation(value = "商品内容管理-一键上架")
    @PostMapping("/batchShelfGoods")
    public ResponseEntity<Void>  batchShelfGoods(@LoginUser Long userCode,@RequestBody Map<String,Object> data){
        goodsService.batchShelfGoods(userCode,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation(value = "商品上下架管理-商品上下架列表")
    @GetMapping("/sellGoods")
    public ResponseEntity<PageResult<Goods>>  sellGoods( @ApiParam(value = "商品名称", name = "goodsName") @RequestParam(value = "goodsName",required = false) String  goodsName,
                                                         @ApiParam(value = "商品类型", name = "goodsType") @RequestParam(value = "goodsType",required = false) Integer typeId,
                                                         @ApiParam(value = "页数", name = "pageNo") @RequestParam("pageNo") Integer pageNo,
                                                         @ApiParam(value = "页数大小", name = "pageSize") @RequestParam("pageSize") Integer pageSize){
        return ResponseEntity.ok(goodsService.sellgoods(pageNo,pageSize,goodsName,typeId));
    }


    @ApiOperation(value = "商品内容管理-导出商品")
    @GetMapping("/exportGoodsInfo")
    public ResponseEntity<Void>  exportGoodsInfo(HttpServletResponse response) throws UnsupportedEncodingException {
        goodsService.exportGoodsInfo(null,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品上下架管理-修改热度")
    @PostMapping("/updateGoodsInfoHot")
    public ResponseEntity<Void>  updateGoodsInfoHot(@LoginUser Long userId,@RequestBody  Map<String, Object> data){
        goodsService.updateGoodsInfoHot(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品上下架管理-是否推荐")
    @PostMapping("/isRecommend")
    public ResponseEntity<Void>  isRecommend(@LoginUser Long userId,@RequestBody  Map<String, Object> data){
        goodsService.isRecommend(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品上下架管理-设置新品")
    @PostMapping("/updateGoodsNew")
    public ResponseEntity<Void>  updateGoodsNew(@LoginUser Long userId,@RequestBody  Map<String, Object> data){
        goodsService.updateGoodsNew(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "商品上下架管理-导入商品")
    @PostMapping("/importGoodsInfo")
    public ResponseEntity<Void>  importGoodsInfo(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws IOException {
        goodsService.importGoodsInfo(file,request,response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
