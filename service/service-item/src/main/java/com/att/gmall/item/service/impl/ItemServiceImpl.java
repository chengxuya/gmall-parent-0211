package com.att.gmall.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.att.gmall.product.client.ProductFeignClient;
import com.att.gmall.item.service.ItemService;
import com.att.gmall.list.client.ListFeignClient;
import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.model.product.SpuSaleAttr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    ListFeignClient ListFeignClient;
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
        Map<String, Object> map = new HashMap<>();

//        long start = System.currentTimeMillis();

        // 1
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);

        // 2
        BaseCategoryView categoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());

        // 3
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId,spuId);

        // 4
        List<Map<String, Object>> valueSkuIdMapList = productFeignClient.getSkuValueIdsMap(spuId);//mybatis默认返回map对象,要用list接收
        Map<String, String> valueSkuIdMap = new HashMap<>();
        for (Map<String, Object> stringObjectMap : valueSkuIdMapList) {
            String v_sku_id = stringObjectMap.get("sku_id") + "";
            String k_value_ids = stringObjectMap.get("value_ids") + "";
            valueSkuIdMap.put(k_value_ids, v_sku_id);
        }

        // 5
        BigDecimal price = productFeignClient.getSkuPrice(skuId);

        map.put("skuInfo",skuInfo);
        map.put("categoryView",categoryView);
        map.put("spuSaleAttrList",spuSaleAttrList);
        map.put("valueSkuIdMap", JSON.toJSONString(valueSkuIdMap));

        map.put("price",price);
//        long end = System.currentTimeMillis();
//        System.out.println("非多线程的时间是 ==>> " + (end - start));
        return map;
    }

    @Override
    public Map<String, Object> getItemThread(Long skuId) {
        Map<String, Object> map = new HashMap<>();
        // 商品基本信息和图片信息
//        long start = System.currentTimeMillis();
        CompletableFuture<SkuInfo> completableFutureSkuInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>() {
            @Override
            public SkuInfo get() {
                SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
                map.put("skuInfo", skuInfo);
                return skuInfo;
            }
        },executor);
        //商品分类信息
        CompletableFuture completableFutureCategoryView = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
                map.put("categoryView", baseCategoryView);
            }
        },executor);
        //商品销售属性列表
        CompletableFuture completableSpuSaleAttrList = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<SpuSaleAttr> spuSaleAttrList = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
                map.put("spuSaleAttrList", spuSaleAttrList);
            }
        },executor);
        //商品的销售属性值对应skuId的map
        CompletableFuture completableValueAttr = completableFutureSkuInfo.thenAcceptAsync(new Consumer<SkuInfo>() {
            @Override
            public void accept(SkuInfo skuInfo) {
                List<Map<String, Object>> valueSkuIdMapList = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());//mybatis默认返回map对象,要用list接收
                Map<String, String> valueSkuIdMap = new HashMap<>();
                for (Map<String, Object> stringObjectMap : valueSkuIdMapList) {
                    String v_sku_id = stringObjectMap.get("sku_id") + "";
                    String k_value_ids = stringObjectMap.get("value_ids") + "";
                    valueSkuIdMap.put(k_value_ids, v_sku_id);
                }
                map.put("valuesSkuJson", JSON.toJSONString(valueSkuIdMap));
            }
        },executor);
        //价格信息
        CompletableFuture completableFuturePrice = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                BigDecimal price = productFeignClient.getSkuPrice(skuId);
                map.put("price", price);
            }
        },executor);
        // 调用商品搜索服务，更新热度值
        CompletableFuture completableFutureHotScore  = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                ListFeignClient.incrHotScore(skuId);
            }
        }, executor);
        CompletableFuture.allOf(completableFutureSkuInfo, completableFutureCategoryView, completableSpuSaleAttrList, completableValueAttr, completableFuturePrice).join();
//        long end = System.currentTimeMillis();
//        System.out.println(end - start + "多线程");


        return map;
    }
}
