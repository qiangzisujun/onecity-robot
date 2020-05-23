package com.tangchao.shop.web;

import com.tangchao.shop.service.TongjiBaiduService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Class TongjiBaiduController
 * @Description TODO
 * @Author Aquan
 * @Date 2020/5/6 10:20
 * @Version 1.0
 **/
@RequestMapping("/api/tongji")
@RestController
@Api(tags = "百度统计模块")
public class TongjiBaiduController {

    private final TongjiBaiduService tongjiBaiduService;

    public TongjiBaiduController(TongjiBaiduService tongjiBaiduService) {
        this.tongjiBaiduService = tongjiBaiduService;
    }

    @ApiOperation(value = "获取刷新百度统计AccessToken")
    @GetMapping("/getAccessToken")
    public ResponseEntity getAccessToken() {
        return ResponseEntity.ok(tongjiBaiduService.getAccessToken());
    }

    @ApiOperation(value = "获取站点的网站趋势")
    @GetMapping("/getTimeTrendRpt")
    public ResponseEntity getTimeTrendRpt(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return ResponseEntity.ok(tongjiBaiduService.getTimeTrendRpt(startDate, endDate));
    }

    @ApiOperation(value = "获取站点的全部来源")
    @GetMapping("/getSourceAll")
    public ResponseEntity getSourceAll(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return ResponseEntity.ok(tongjiBaiduService.getSourceAll(startDate, endDate));

    }

    @ApiOperation(value = "获取站点的受访页面")
    @GetMapping("/getVisitToppage")
    public ResponseEntity getVisitToppage(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return ResponseEntity.ok(tongjiBaiduService.getVisitToppage(startDate, endDate));

    }

    @ApiOperation(value = "获取站点的地域分布(按省)")
    @GetMapping("/getVisitDistrict")
    public ResponseEntity getVisitDistrict(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) {
        return ResponseEntity.ok(tongjiBaiduService.getVisitDistrict(startDate, endDate));

    }

}
