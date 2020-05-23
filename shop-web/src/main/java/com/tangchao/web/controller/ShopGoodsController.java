package com.tangchao.web.controller;


import com.tangchao.shop.pojo.Lottery;
import com.tangchao.shop.pojo.ShopGoodsType;
import com.tangchao.shop.service.LotteryService;
import com.tangchao.shop.service.ShopGoodsService;
import com.tangchao.shop.service.ShopGoodsTypeService;
import com.tangchao.shop.vo.ShopGoodsVO;
import com.tangchao.shop.vo.TrendChartVO;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/goods")
@RestController
@Api(value = "商品模块", tags = {"商品模块"})
public class ShopGoodsController {

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private LotteryService lotteryService;

    @Autowired
    private ShopGoodsTypeService shopGoodsTypeService;

    @ApiOperation(value = "获取商品详情")
    @GetMapping("getGoodsDetail")
    public ResponseEntity<ShopGoodsVO> getGoodsList(Long goodsId) {
        return ResponseEntity.ok(shopGoodsService.getShopGoodsByGid(goodsId));
    }

    @ApiOperation(value = "走势图商品信息")
    @GetMapping("selectTrendChart")
    public ResponseEntity<Map<String,Object>> selectTrendChart(@ApiParam(value = "商品唯一编码",name ="goodsNo")@RequestParam(value = "goodsNo")String goodsNo) {
        return ResponseEntity.ok(lotteryService.selectTrendChart(goodsNo));
    }

    @ApiOperation(value = "走势图监控")
    @GetMapping("goodsTrendListInfo")
    public ResponseEntity<List<Lottery>> goodsTrendListInfo(@ApiParam(value = "商品唯一编码",name ="goodsNo")@RequestParam(value = "goodsNo") String goodsNo) {
        return ResponseEntity.ok(lotteryService.goodsTrendListInfo(goodsNo));
    }

    @ApiOperation(value = "分类目录列表")
    @GetMapping("getCategories")
    public ResponseEntity<List<ShopGoodsType>>  goodsTypeList(){
        return ResponseEntity.ok(shopGoodsTypeService.goodsTypeList());
    }


    @ApiOperation(value = "分类商品列表")
    @GetMapping("getTypeGoodsList")
    public ResponseEntity getUserCouponList(@ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                            @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                                            @ApiParam(value = "分类ID", name = "typeId", defaultValue = "0") @RequestParam("typeId") Integer typeId) {
        return ResponseEntity.ok(shopGoodsTypeService.getUserCouponList(pageNo, pageSize, typeId));
    }

    @ApiOperation(value = "特殊商品列表")
    @GetMapping("getSpecialGoodsList")
    public ResponseEntity getSpecialGoodsList(@ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                              @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                                              @ApiParam(value = "类别：1限时特价 2热销 3严选 4首页推荐", name = "type", defaultValue = "1") @RequestParam("type") Integer type) {
        return ResponseEntity.ok(shopGoodsService.getSpecialGoodsList(pageNo, pageSize, type));
    }

    @ApiOperation(value = "商品限购次数")
    @GetMapping("getGoodsLimitNumByGoodsNo")
    public ResponseEntity getGoodsLimitNumByGoodsNo(@LoginUser Long userCode,@ApiParam(value = "商品编号",name = "goodsNo") @RequestParam(value = "goodsNo") String goodsNo) {
        return ResponseEntity.ok(shopGoodsService.getGoodsLimitNumByGoodsNo(userCode,goodsNo));
    }
}
