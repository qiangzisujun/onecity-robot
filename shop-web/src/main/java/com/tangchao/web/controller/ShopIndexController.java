package com.tangchao.web.controller;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.pojo.UserConf;
import com.tangchao.shop.pojo.UserProtocol;
import com.tangchao.shop.service.IndexService;
import com.tangchao.shop.service.ShopGoodsService;
import com.tangchao.shop.service.UserProtocolService;
import com.tangchao.shop.vo.BannerAndCategory;
import com.tangchao.shop.vo.ShopGoodsVO;
import com.tangchao.user.service.CmsConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/index")
@RestController
@Api(value = "钻石商城首页模块", tags = {"钻石商城首页模块"})
public class ShopIndexController {

    @Autowired
    private IndexService indexService;

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private UserProtocolService userProtocolService;

    @Autowired
    private CmsConfigService cmsConfigService;


    @Deprecated
    @ApiOperation(value = "新商城广告和分类")
    @GetMapping("/getBannerAndCategory")
    public ResponseEntity<BannerAndCategory> getBannerAndCategory() {
        return ResponseEntity.ok(indexService.getBannerAndCategoryInfo());
    }


    @Deprecated
    @ApiOperation(value = "获取商品列表")
    @GetMapping("/getGoodsList")
    public ResponseEntity<List<ShopGoodsVO>> getGoodsList(Integer page, Integer rows, Long cid1) {
        return ResponseEntity.ok(shopGoodsService.getShopGoodsByPage(page, rows, cid1));
    }

    @ApiOperation(value = "钻石商场首页广告轮播图")
    @GetMapping("getBannerList")
    public ResponseEntity<List<Map<String, Object>>> getBannerList() {
        return ResponseEntity.ok(indexService.getZSBannerList());
    }

    @ApiOperation(value = "后台钻石商城商品列表")
    @GetMapping("getList")
    public ResponseEntity<PageResult<ShopGoods>> getList(@ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                                      @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                                                      @ApiParam(value = "商品名称", name = "title") @RequestParam(value = "title", required = false) String title){
        PageResult<ShopGoods> result = shopGoodsService.getList(pageNo, pageSize, title);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "获取钻石商城商品详情信息")
    @GetMapping("getInfo")
    public ResponseEntity getInfo(@ApiParam(value = "商品id", name = "id") @RequestParam("id") Long id){
        return shopGoodsService.getInfo(id);
    }

    @ApiOperation("获取用户协议")
    @GetMapping("/show")
    private ResponseEntity<List<UserProtocol>> getUserProtocol(){
        return ResponseEntity.ok(userProtocolService.getUserProtocol());
    }

    @ApiOperation("获取各种背景图")
    @GetMapping("/getBgImages")
    private ResponseEntity<UserConf> getBgImages(@ApiParam(value = "后台配置键",name = "key") @RequestParam("key") String key){
        return ResponseEntity.ok(cmsConfigService.selectCmsValue(key));
    }

    @ApiOperation("获取各种广告图")
    @GetMapping("/getBannersImages")
    private ResponseEntity<List<Map<String,Object>>> getBannersImages(@ApiParam(value = "后台配置键",name = "key") @RequestParam("key") String key){
        return ResponseEntity.ok(indexService.showZSBannerList(key));
    }

}
