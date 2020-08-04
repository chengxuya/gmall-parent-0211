package com.att.gmall.list.controller;

import com.att.gmall.common.result.Result;
import com.att.gmall.list.service.ListApiService;
import com.att.gmall.model.list.Goods;
import com.att.gmall.model.list.SearchParam;
import com.att.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/list")
public class ListApiController {
        @Autowired
        private ElasticsearchRestTemplate elasticsearchRestTemplate;
        @Autowired
        private ListApiService listApiService;
    @GetMapping("createIndex")
    public Result createIndex() {
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }
    @RequestMapping("inner/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId){
        listApiService.upperGoods(skuId);
        return  Result.ok();
    }
    @RequestMapping("inner/lowerGoods/{skuId}")
    public  Result lowerGoods(@PathVariable("skuId") Long skuId){
        listApiService.lowerGoods(skuId);
        return  Result.ok();

    }
    @RequestMapping("inner/incrHotScore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId") Long skuId){
        listApiService.incrHotScore(skuId);
        return  Result.ok();
    }

    @RequestMapping
    public Result list(@RequestBody SearchParam searchParam){
        SearchResponseVo searchResponseVo= listApiService.list(searchParam);
        return  Result.ok(searchResponseVo);
    }
}

