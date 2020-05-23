package com.tangchao.web.controller;


import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.dto.GoodsStageDTO;
import com.tangchao.shop.pojo.Annc;
import com.tangchao.shop.pojo.GoodsStage;
import com.tangchao.shop.service.AnncService;
import com.tangchao.shop.service.GoodsStageService;
import com.tangchao.shop.service.IndexService;
import com.tangchao.shop.service.ShopGoodsService;
import com.tangchao.shop.vo.GoodsLotteryVO;
import com.tangchao.shop.vo.GoodsStageVO;
import com.tangchao.shop.vo.GoodsTypeVO;
import com.tangchao.user.service.CmsConfigService;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RequestMapping("/api/index")
@RestController
@Api(value = "元购首页模块", tags = {"元购首页模块"})
public class IndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private AnncService anncService;

    @Autowired
    private GoodsStageService stageService;

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private CmsConfigService configService;


    @ApiOperation(value = "元购广告和分类")
    @GetMapping("/getSysBanner")
    public ResponseEntity<List<Map<String, Object>>> getSysBanner() {
        return ResponseEntity.ok(indexService.getBannerList());
    }

    @ApiOperation(value = "用户协议")
    @GetMapping("getUserNewestAnnc")
    public ResponseEntity<Annc> getUserNewestAnnc() {
        return ResponseEntity.ok(anncService.findUserNewestAnnc());
    }

    @ApiOperation(value = "最新公告")
    @GetMapping("/getNewestAnnc")
    public ResponseEntity<Annc> getNewestAnnc() {
        return ResponseEntity.ok(anncService.findNewestAnnc());
    }

    @ApiOperation(value = "历史公告")
    @GetMapping("/getHistoryAnnc")
    public ResponseEntity<List<Annc>> getHistoryAnnc() {
        return ResponseEntity.ok(anncService.selectHistoryAnnc());
    }


    @ApiOperation(value = "最新揭晓")
    @GetMapping("/list/open")
    public ResponseEntity<List<GoodsStage>> selectOpeningGoodsList() {
        return ResponseEntity.ok(stageService.selectOpeningGoodsList());
    }

    @ApiOperation(value = "开奖商品信息")
    @GetMapping("/list/open/info")
    public ResponseEntity<Map<String,Object>> OpenGoods(@ApiParam(value = "商品期数Id",name = "stageId") Long stageId) {
        return ResponseEntity.ok(stageService.selectOpenGoods(stageId));
    }


    /**
     * 开奖商品列表
     *
     * @param fullTimeMsec
     * @return
     */
    @ApiOperation(value = "开奖商品列表")
    @GetMapping("/selectOpeningGoodsList")
    public ResponseEntity<PageResult<GoodsLotteryVO>> OpenGoodsFullList(@ApiParam(value = "页数", name = "pageNo") @RequestParam(value = "pageNo") Integer pageNo,
                                                                        @ApiParam(value = "页数大小", name = "pageSize") @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @ApiParam(value = "开奖时间", name = "fullTimeMsec") Long fullTimeMsec) {
        Date fullTime = null;
        if (fullTimeMsec != null) {
            fullTime = new Date(fullTimeMsec);
        }
        return ResponseEntity.ok(stageService.selectOpenGoodsList(pageNo, pageSize, fullTime));// 查新最新开奖商品列表
    }

    @ApiOperation(value = "获取商品期数列表")
    @PostMapping("/getGoodsStageList")
    public ResponseEntity<PageResult<GoodsStageVO>> getGoodsStageList(@LoginUser Long userCode,@RequestBody GoodsStageDTO stageDTO) {
        return ResponseEntity.ok(shopGoodsService.selectGoodsList(stageDTO,userCode));
    }

    @ApiOperation(value = "获取商品分类列表")
    @GetMapping("/queryGoodsTypeList")
    public ResponseEntity<List<GoodsTypeVO>> queryGoodsTypeList() {
        return ResponseEntity.ok(shopGoodsService.queryGoodsTypeList());
    }

    @ApiOperation(value = "商城+元购开关")
    @GetMapping("/getMallSwitch")
    public ResponseEntity<String> getMallSwitch() {
        return ResponseEntity.ok(configService.getMallSwitch());
    }


    @ApiOperation(value = "导航图标")
    @GetMapping("/getNavigationList")
    private ResponseEntity<List<Map<String, Object>>> getNavigationList(){
        return ResponseEntity.ok(indexService.getNavigationList());
    }

    @ApiOperation(value = "网站状态")
    @GetMapping("/getWebSiteStatus")
    private ResponseEntity<Map<String, Object>> getWebSiteStatus(){
        return ResponseEntity.ok(indexService.getWebSiteStatus());
    }

    @ApiOperation(value = "APP下载地址")
    @GetMapping("/getDownloadAPPURL")
    private ResponseEntity<Map<String, Object>> getDownloadAPPURL(){
        return ResponseEntity.ok(indexService.getDownloadAPPURL());
    }


}
