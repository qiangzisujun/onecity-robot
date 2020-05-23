package com.tangchao.shop.web;

import com.github.pagehelper.PageInfo;
import com.tangchao.shop.annotation.LoginUser;
import com.tangchao.shop.pojo.AdvertGroup;
import com.tangchao.shop.pojo.Annc;
import com.tangchao.shop.pojo.CmsAdvert;
import com.tangchao.shop.pojo.MailConf;
import com.tangchao.shop.service.AdvertisementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/* *
 * @Author qiangzi
 * @Description
 * @Date 2019/11/9 20:00
 */
@Api(value = "广告位管理/公告管理", tags = {"广告位管理/公告管理"})
@RequestMapping("/api/banner/")
@RestController
public class BannerController {

    @Autowired
    private AdvertisementService advertisementService;

    @ApiOperation("广告位管理--广告组--列表")
    @GetMapping("/getBannerList")
    public ResponseEntity<PageInfo> getBannerList(@LoginUser Long userId,
                                               @ApiParam(value = "页数",name = "页数") @RequestParam(value = "pageNo") Integer pageNo,
                                               @ApiParam(value = "页数大小",name = "页数大小") @RequestParam(value = "pageSize") Integer pageSize){
        return ResponseEntity.ok(advertisementService.selectList(userId,pageNo,pageSize));
    }

    @ApiOperation("广告位管理--广告组--编辑")
    @PostMapping("/updateBannerInfo")
    public ResponseEntity<Void> updateBannerInfo(@LoginUser Long userId, @RequestBody AdvertGroup advertGroup){
        advertisementService.updateBannerInfo(userId,advertGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("广告位管理--广告组--删除")
    @PostMapping("/deleteBannerInfo")
    public ResponseEntity<Void> deleteBannerInfo(@LoginUser Long userId, @ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        advertisementService.deleteBannerInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("广告位管理--广告组--新增")
    @PostMapping("/insertBannerInfo")
    public ResponseEntity<Void> insertBannerInfo(@LoginUser Long userId, @RequestBody AdvertGroup advertGroup){
        advertisementService.insertBannerInfo(userId,advertGroup);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @ApiOperation("广告位管理--广告--列表")
    @GetMapping("/getCmsAdvertList")
    public ResponseEntity<PageInfo> getCmsAdvertList(@LoginUser Long userId,
                                                  @ApiParam(value = "页数",name = "页数") @RequestParam(value = "pageNo") Integer pageNo,
                                                  @ApiParam(value = "页数大小",name = "页数大小") @RequestParam(value = "pageSize") Integer pageSize){
        return ResponseEntity.ok(advertisementService.getCmsAdvertList(userId,pageNo,pageSize));
    }

    @ApiOperation("广告位管理--广告--编辑")
    @PostMapping("/updateCmsAdvertList")
    public ResponseEntity<Void> updateCmsAdvertList(@LoginUser Long userId,@RequestBody CmsAdvert advert){
        advertisementService.updateCmsAdvertList(userId,advert);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("广告位管理--广告--删除")
    @PostMapping("/deleteCmsAdvertInfo")
    public ResponseEntity<Void> deleteCmsAdvertInfo(@LoginUser Long userId,@ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        advertisementService.deleteCmsAdvertInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("广告位管理--广告--新增")
    @PostMapping("/insertCmsAdvertInfo")
    public ResponseEntity<Void> insertCmsAdvertInfo(@LoginUser Long userId,@RequestBody CmsAdvert advert){
        advertisementService.insertCmsAdvertInfo(userId,advert);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("公告管理--列表")
    @GetMapping("/getAnnouncementList")
    public ResponseEntity<PageInfo> getAnnouncementList(@LoginUser Long userId,
                                                    @ApiParam(value = "页数",name = "页数") @RequestParam(value = "pageNo") Integer pageNo,
                                                    @ApiParam(value = "页数大小",name = "页数大小") @RequestParam(value = "pageSize") Integer pageSize){
        return ResponseEntity.ok(advertisementService.getAnnouncementList(userId,pageNo,pageSize));
    }

    @ApiOperation("公告管理--编辑")
    @PostMapping("/updateAnnouncement")
    public ResponseEntity<Void> updateAnnouncement(@LoginUser Long userId,@RequestBody Annc annc){
        advertisementService.updateAnnouncement(userId,annc);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("公告管理--删除")
    @PostMapping("/deleteAnnouncement")
    public ResponseEntity<Void> deleteAnnouncement(@LoginUser Long userId,@ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        advertisementService.deleteAnnouncement(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("公告管理--新增")
    @PostMapping("/insertAnnouncement")
    public ResponseEntity<Void> insertAnnouncement(@LoginUser Long userId,@RequestBody Annc annc){
        advertisementService.insertAnnouncement(userId,annc);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("邮箱设置--列表")
    @GetMapping("/getMailboxList")
    public ResponseEntity<PageInfo> getMailboxList(@LoginUser Long userId,
                                                   @ApiParam(value = "页数",name = "页数") @RequestParam(value = "pageNo") Integer pageNo,
                                                   @ApiParam(value = "页数大小",name = "页数大小") @RequestParam(value = "pageSize") Integer pageSize){
        return ResponseEntity.ok(advertisementService.getMailboxList(userId,pageNo,pageSize));
    }

    @ApiOperation("邮箱设置--编辑")
    @PostMapping("/updateMailboxInfo")
    public ResponseEntity<PageInfo> updateMailboxInfo(@LoginUser Long userId,@RequestBody MailConf mail){
        advertisementService.updateMailboxInfo(userId,mail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("邮箱设置--删除")
    @PostMapping("/deleteMailboxInfo")
    public ResponseEntity<PageInfo> deleteMailboxInfo(@LoginUser Long userId,@ApiParam(value = "id",name = "id") @RequestBody Map<String,Object> data){
        advertisementService.deleteMailboxInfo(userId,data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @ApiOperation("邮箱设置--新增")
    @PostMapping("/insertMailboxInfo")
    public ResponseEntity<PageInfo> insertMailboxInfo(@LoginUser Long userId,@RequestBody MailConf mail){
        advertisementService.insertMailboxInfo(userId,mail);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
