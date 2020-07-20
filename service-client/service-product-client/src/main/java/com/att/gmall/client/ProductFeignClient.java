package com.att.gmall.client;

import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.model.product.SpuSaleAttr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

//    @RequestMapping("api/product/getItem/{skuId}")
//    Map<String, Object> getItem(Long skuId);
}
