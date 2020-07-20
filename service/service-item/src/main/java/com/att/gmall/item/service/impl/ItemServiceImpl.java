package com.att.gmall.item.service.impl;

import com.att.gmall.client.ProductFeignClient;
import com.att.gmall.item.service.ItemService;
import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemServiceImpl implements ItemService {
     @Autowired
     ProductFeignClient productFeignClient;
    //1 商品基本信息
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
        SkuInfo skuInfo=productFeignClient.getSkuInfo(skuId);
     BaseCategoryView baseCategoryView= productFeignClient.getCategoryView(skuInfo.getCategory3Id());
     map.put("categoryView", baseCategoryView);
     map.put("skuInfo", skuInfo);
        map.put("price",skuInfo.getPrice());
        return map;
    }
}
