package com.att.gmall.product.controller;


import com.att.gmall.common.result.Result;
import com.att.gmall.model.product.BaseCategory1;
import com.att.gmall.model.product.BaseCategory2;
import com.att.gmall.model.product.BaseCategory3;
import com.att.gmall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @RequestMapping("getCategory1")
    public Result getCategory1(){
     List<BaseCategory1> category1s=categoryService.getCategory1();
        return Result.ok(category1s);
    }
    @RequestMapping("getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") String category1Id){
        List<BaseCategory2> category2s=categoryService.getCategory2(category1Id);
        return Result.ok(category2s);
    }
    @RequestMapping("getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id")  String category2Id){
        List<BaseCategory3> category3s=categoryService.getCategory3(category2Id);
        return Result.ok(category3s);
    }

    
}
