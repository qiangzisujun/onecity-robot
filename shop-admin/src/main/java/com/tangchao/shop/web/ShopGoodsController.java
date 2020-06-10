package com.tangchao.shop.web;

import com.tangchao.common.vo.PageResult;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.dto.ShopCouponDTO;
import com.tangchao.shop.params.ShopGoodsParam;
import com.tangchao.shop.pojo.ShopGoods;
import com.tangchao.shop.pojo.ShopGoodsType;
import com.tangchao.shop.pojo.ShopSpecGroup;
import com.tangchao.shop.service.CouponService;
import com.tangchao.shop.service.ShopGoodsGroupService;
import com.tangchao.shop.service.ShopGoodsService;
import com.tangchao.shop.service.ShopGoodsTypeService;
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
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/shop")
@RestController
@Api(value = "钻石商城商品模块", tags = {"钻石商城商品模块"})
public class ShopGoodsController {

    @Autowired
    private ShopGoodsService shopGoodsService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ShopGoodsTypeService shopGoodsTypeService;

    @Autowired
    private ShopGoodsGroupService shopGoodsGroupService;

    @ApiOperation(value = "添加钻石商城商品")
    @PostMapping("add")
    public ResponseEntity add(@LoginUser Long userId, @RequestBody ShopGoodsParam shopGoodsParam){
        return shopGoodsService.add(userId, shopGoodsParam);
    }

    @ApiOperation(value = "获取钻石商城商品详情信息")
    @GetMapping("getBy")
    public ResponseEntity getBy(@LoginUser Long userId,
                                @ApiParam(value = "商品id", name = "id") @RequestParam("id") Long id){
        return shopGoodsService.getBy(userId, id);
    }

    @ApiOperation(value = "后台钻石商城商品列表")
    @GetMapping("list")
    public ResponseEntity<PageResult<ShopGoods>> list(@LoginUser Long userId,
                                                      @ApiParam(value = "页数", name = "pageNo", defaultValue = "0") @RequestParam("pageNo") Integer pageNo,
                                                      @ApiParam(value = "页数大小", name = "pageSize", defaultValue = "10") @RequestParam("pageSize") Integer pageSize,
                                                      @ApiParam(value = "商品名称", name = "title") @RequestParam(value = "title", required = false) String title,
                                                      @ApiParam(value = "商品分类Id", name = "typeId") @RequestParam(value = "typeId", required = false) String typeId,
                                                      @ApiParam(value = "1秒杀，2严选、", name = "type") @RequestParam(value = "type", required = false) Integer type){
        PageResult<ShopGoods> result = shopGoodsService.list(userId, pageNo, pageSize, title,typeId,type);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "钻石商城商品上下架")
    @PostMapping("standUpDown")
    public ResponseEntity standUpDown(@LoginUser Long userId,
                                      @ApiParam(value = "商品id", name = "id") @RequestParam("id") Long id,
                                      @ApiParam(value = "上下架操作：true上架/false下架", name = "operate", defaultValue = "true") @RequestParam("operate") Boolean operate){
        return shopGoodsService.standUpDown(userId, id, operate);
    }

    @ApiOperation(value = "钻石商城商品首页推荐")
    @PostMapping("isHome")
    public ResponseEntity isHome(@LoginUser Long userId,
                                      @ApiParam(value = "商品id", name = "id") @RequestParam("id") Long id,
                                      @ApiParam(value = "0不是，1是", name = "operate", defaultValue = "0") @RequestParam("operate") Integer operate){
        return shopGoodsService.isHome(userId, id, operate);
    }

    @ApiOperation(value = "删除钻石商城商品")
    @PostMapping("deleteBy")
    public ResponseEntity deleteBy(@LoginUser Long userId,
                                   @ApiParam(value = "商品id", name = "id") @RequestParam("id") Long id){
        return shopGoodsService.deleteBy(userId, id);
    }

    @ApiOperation(value = "更新钻石商城商品")
    @PostMapping("updateBy")
    public ResponseEntity updateBy(@LoginUser Long userId,
                                   @RequestBody ShopGoodsParam shopGoodsParam){
        return shopGoodsService.updateBy(userId, shopGoodsParam);
    }

    @ApiOperation(value = "保存优惠券信息")
    @PostMapping("saveCouponInfo")
    public ResponseEntity<Void> saveCouponInfo(@LoginUser Long userId,@RequestBody  ShopCouponDTO shopCouponDTO){
        shopCouponDTO.setUserId(userId);
        couponService.saveCouponInfo(shopCouponDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改优惠券信息")
    @PostMapping("updateCouponInfo")
    public ResponseEntity<Void> updateCouponInfo(@LoginUser Long userId,@RequestBody ShopCouponDTO shopCouponDTO){
        shopCouponDTO.setUserId(userId);
        couponService.updateCouponInfo(shopCouponDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "删除优惠券信息")
    @PostMapping("deleteCouponInfo")
    public ResponseEntity<Void> deleteCouponInfo(@LoginUser Long userId, @RequestBody Map<String,Object> id){
        couponService.deleteCouponInfo(userId,id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "优惠券信息列表")
    @GetMapping("shopCouponList")
    public ResponseEntity shopCouponList(@LoginUser Long userId,Integer pageNo, Integer pageSize,String name){
        return couponService.getCouponListByAdmin(userId,pageNo,pageSize,name);
    }


    @ApiOperation(value = "商品分类列表")
    @GetMapping("/goodsTypeList")
    public ResponseEntity<List<ShopGoodsType>>  goodsTypeList(){
        return ResponseEntity.ok(shopGoodsTypeService.goodsTypeList());
    }

    @ApiOperation(value = "新增商品分类")
    @PostMapping("/addGoodsType")
    public ResponseEntity<Void>  addGoodsType(@LoginUser Long userId,@RequestBody ShopGoodsType type){
        shopGoodsTypeService.addGoodsType(type.getTypeNameZh(),type.getTypePid(),userId,type.getTypeNameCn(),type.getTypeNameMa());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改商品分类")
    @PostMapping("/updateGoodsType")
    public ResponseEntity<Void>  updateGoodsType(@RequestBody ShopGoodsType goodsType){
        shopGoodsTypeService.updateGoodsType(goodsType );
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改排序")
    @PostMapping("/updateSort")
    public ResponseEntity<Void>  updateSort(@RequestBody Map<String,Object> data){
        Long id1=Long.valueOf(data.get("id1").toString());
        Long id2=Long.valueOf(data.get("id2").toString());
        shopGoodsTypeService.updateSort(id1,id2);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "删除商品分类")
    @GetMapping("/deleteGoodsType")
    public ResponseEntity<Void>  deleteGoodsType(@ApiParam(value = "商品分类Id", name = "id") @RequestParam(value = "id") Long id){
        shopGoodsTypeService.deleteGoodsType(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "查询商品规格组")
    @PostMapping("/selectGoodsGroup")
    public ResponseEntity<List<ShopSpecGroup>>  selectGoodsGroup(@LoginUser Long userId){
        return ResponseEntity.ok(shopGoodsGroupService.selectGoodsGroup(userId));
    }

    @ApiOperation(value = "新增商品规格组")
    @PostMapping("/addGoodsGroup")
    public ResponseEntity<Void>  addGoodsGroup(@LoginUser Long userId,@RequestBody ShopSpecGroup group){
        shopGoodsGroupService.addGoodsGroup(group);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "修改商品规格组")
    @PostMapping("/updateGoodsGroup")
    public ResponseEntity<Void>  updateGoodsGroup(@RequestBody ShopSpecGroup group){
        shopGoodsGroupService.updateGoodsGroup(group);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation(value = "删除商品规格组")
    @GetMapping("/deleteGoodsGroup")
    public ResponseEntity<Void>  deleteGoodsGroup(@ApiParam(value = "商品分类Id", name = "id") @RequestParam(value = "id") Long id){
        shopGoodsGroupService.deleteGoodsType(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "钻石商城商品批量导入")
    @PostMapping("batchImport")
    public ResponseEntity batchImport(//@LoginUser Long userId,
                                      @RequestParam("SensitiveExcle") MultipartFile[] files,
                                      HttpServletRequest request){
        return shopGoodsService.batchImport(files, request);
    }

    @ApiOperation(value = "钻石商城商品批量导出")
    @GetMapping("/batchExport")
    public ResponseEntity<Void> batchExport(//@LoginUser Long userId,
                                            HttpServletResponse response) {
        shopGoodsService.batchExport(response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation(value = "批量设置严选,秒杀")
    @PostMapping("/batchSetShop")
    public ResponseEntity<Void> batchSetShop(@RequestBody Map<String,Object> data) {
        shopGoodsService.batchSetShop(data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
