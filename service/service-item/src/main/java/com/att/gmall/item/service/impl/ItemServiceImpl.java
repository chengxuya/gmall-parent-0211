package com.att.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.att.gmall.client.ProductFeignClient;
import com.att.gmall.item.service.ItemService;
import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
     @Autowired
     ProductFeignClient productFeignClient;

     @Autowired
    RedisTemplate redisTemplate;
    // 1 商品基本信息
    //用缓存
    //
    //2 商品图片信息
    //用缓存
    //
    //3 商品的销售属性信息
    //用缓存
    //
    //4 商品分类信息
    //用缓存
    //
    //5 商品的价格信息
    //查库

    @Override
    public Map<String, Object> getItem(Long skuId) {
        Map<String,Object> map=new HashMap<>();
        // 商品基本信息和图片信息
        SkuInfo skuInfo=productFeignClient.getSkuInfo(skuId);

        //商品分类信息
     BaseCategoryView baseCategoryView= productFeignClient.getCategoryView(skuInfo.getCategory3Id());

         //价格信息
       BigDecimal price= productFeignClient.getSkuPrice(skuId);

       //商品销售属性列表
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrList =productFeignClient.getSpuSaleAttrListCheckBySku(skuId,spuId);

       //商品的销售属性值对应skuId的map
              List<Map<String,Object>> valueSkuIdMapList=  productFeignClient.getSkuValueIdsMap(spuId);//mybatis默认返回map对象,要用list接收
        Map<String,String> valueSkuIdMap=new HashMap<>();
        for (Map<String, Object> stringObjectMap : valueSkuIdMapList) {
             String v_sku_id=  stringObjectMap.get("sku_id")+"";
             String k_value_ids=  stringObjectMap.get("value_ids")+"";
                    valueSkuIdMap.put(k_value_ids,v_sku_id);
        }
     map.put("categoryView", baseCategoryView);
     map.put("skuInfo", skuInfo);
        map.put("price",price);
        map.put("spuSaleAttrList",spuSaleAttrList);
        map.put("valuesSkuJson", JSON.toJSONString(valueSkuIdMap));
        return map;
    }
}
