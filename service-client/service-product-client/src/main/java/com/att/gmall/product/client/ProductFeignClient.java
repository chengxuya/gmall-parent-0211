package com.att.gmall.product.client;

import com.alibaba.fastjson.JSONObject;
import com.att.gmall.common.result.Result;
import com.att.gmall.model.list.SearchAttr;
import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.BaseTrademark;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("service-product")
public interface ProductFeignClient {
    @GetMapping("/api/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getCategoryView/{category3Id}")
    BaseCategoryView getCategoryView(@PathVariable("category3Id")Long category3Id);

    @GetMapping("/api/product/inner/getSkuPrice/{skuId}")
    BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);

    @GetMapping("/api/product/inner/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId,@PathVariable("spuId") Long spuId);

    @GetMapping("/api/product/inner/getSkuValueIdsMap/{spuId}")
    List<Map<String, Object>> getSkuValueIdsMap(@PathVariable("spuId")  Long spuId);

    @GetMapping("/api/product/inner/getAttrList/{skuId}")
    List<SearchAttr> getAttrList(@PathVariable("skuId")Long skuId);

    @GetMapping("/api/product/inner/getTrademark/{tmId}")
    BaseTrademark getTrademark(@PathVariable("tmId") Long tmId);

    @GetMapping("/api/product/inner/getBaseCategoryList")
    Result getBaseCategoryList();

//    @RequestMapping("api/product/getItem/{skuId}")
//    Map<String, Object> getItem(Long skuId);
}
