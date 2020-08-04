package com.att.gmall.list.client;

import com.att.gmall.common.result.Result;
import com.att.gmall.model.list.SearchParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("service-list")
public interface ListFeignClient {
    @RequestMapping("api/list/inner/upperGoods/{skuId}")
    Result upperGoods(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/inner/lowerGoods/{skuId}")
    Result lowerGoods(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list/inner/incrHotScore/{skuId}")
     Result incrHotScore(@PathVariable("skuId") Long skuId);

    @RequestMapping("api/list")
    Result<Map> list(@RequestBody SearchParam searchParam);
}
