package com.att.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.att.gmall.common.result.Result;
import com.att.gmall.model.list.SearchAttr;
import com.att.gmall.model.product.*;
import com.att.gmall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/product")
public class ProductApiController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuService skuService;

    @Autowired
    SpuService spuService;
    @Autowired
    AttrInfoService attrInfoService;

    @Autowired
    BaseTrademarkService baseTrademarkService;
    @GetMapping("inner/getSkuInfo/{skuId}")
    public  SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
      SkuInfo skuInfo=  skuService.getSkuInfoNx(skuId);

        return skuInfo;
    }

    @GetMapping("inner/getCategoryView/{category3Id}")
    public BaseCategoryView getCategoryView(@PathVariable("category3Id") Long category3Id){
        BaseCategoryView baseCategoryView=  categoryService.getCategoryView(category3Id);
        return baseCategoryView;
    }
    @GetMapping("inner/getSkuPrice/{skuId}")
    public  BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId){
        BigDecimal price=  skuService.getSkuPrice(skuId);
        return price;
    }
    @GetMapping("inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public   List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> spuSaleAttrs = spuService.getSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrs;
    }

    /**
     * 根据spuId 查询map 集合属性
     * @param spuId
     * @return
     */
    @GetMapping("inner/getSkuValueIdsMap/{spuId}")
   public List<Map<String, Object>> getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        List<Map<String, Object>> maps=  skuService.getSkuValueIdsMap(spuId);
       return maps;
    }

    @GetMapping("inner/getAttrList/{skuId}")
    public List<SearchAttr> getAttrList(@PathVariable("skuId")Long skuId){
        List<SearchAttr> SearchAttrs=   attrInfoService.getAttrList(skuId);
   //     Result.ok(baseAttrInfos)
        return SearchAttrs;
    }
    @GetMapping("inner/getTrademark/{tmId}")
    public BaseTrademark getTrademark(@PathVariable("tmId") Long tmId){
       BaseTrademark baseTrademark= baseTrademarkService.getTrademark(tmId);
            return  baseTrademark;
    }
    @GetMapping("inner/getBaseCategoryList")
    public Result getBaseCategoryList(HttpServletRequest request){
        String userId=request.getHeader("userId");
        List<JSONObject> baseCategoryList=categoryService.getBaseCategoryList();
        return Result.ok(baseCategoryList);

    }
}
