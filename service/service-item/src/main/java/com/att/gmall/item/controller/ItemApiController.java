package com.att.gmall.item.controller;

import com.att.gmall.client.ProductFeignClient;
import com.att.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/item")
public class ItemApiController {
    @Autowired
    ProductFeignClient productFeignClient;
    @RequestMapping("{skuId}")
    Result<Map<String, Object>> getItem(@PathVariable("skuId") Long skuId){

        return null;
    }

}
