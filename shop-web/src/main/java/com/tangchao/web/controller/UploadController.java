package com.tangchao.web.controller;

import com.tangchao.shop.service.UploadService;
import com.tangchao.web.annotation.LoginUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@Api(value = "元购图片上传模块", tags = {"元购图片上传模块"})
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @ApiOperation(value = "图片上传")
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(uploadService.uploadImage(file));
    }
}
