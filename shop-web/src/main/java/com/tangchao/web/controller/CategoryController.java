package com.tangchao.web.controller;


import com.tangchao.shop.service.ShopCategoryService;
import com.tangchao.shop.vo.ShopCategoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@Api(value = "分类模块", tags = {"分类模块"})
public class CategoryController {

    @Autowired
    private ShopCategoryService categoryService;

    @ApiOperation(value = "分类列表")
    @GetMapping("/getCategoryList")
    public ResponseEntity<List<ShopCategoryVO>> getCategoryList(@RequestParam(value = "cateId", required = false) Long cateId) {
        return ResponseEntity.ok(categoryService.getShopCategoryList(cateId));
    }
}
