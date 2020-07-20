package com.att.gmall.product.controller;

import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.product.service.CategoryService;
import com.att.gmall.product.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/product")
public class ProductApiController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuService skuService;

    @GetMapping("inner/getSkuInfo/{skuId}")
    public  SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
      SkuInfo skuInfo=  skuService.getSkuInfo(skuId);

        return skuInfo;
    }

    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView=  categoryService.getCategoryView(category3Id);
        return baseCategoryView;
    }
    /**
     * 根据spuId 查询map 集合属性
     * @param spuId
     * @return
     */
//    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
//    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
//        return null;
//    }
}
