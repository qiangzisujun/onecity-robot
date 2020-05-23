package com.tangchao.web.controller;

import com.tangchao.shop.service.ConfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/conf")
@RestController
@Api(value = "配置模块", tags = {"配置模块"})
public class ConfController {

    @Autowired
    private ConfService confService;

    @ApiOperation("热搜关键字")
    @GetMapping(value = "/getHostKey")
    public ResponseEntity<List<String>> getHostKey() {
        return ResponseEntity.ok(confService.selectHotSearchKey());
    }
}
