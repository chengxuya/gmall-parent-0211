package com.att.gmall.item.client;


import com.att.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(value="service-item")
public interface ItemFeignClient {//第二类url  页面可以通过ajax访问的url  /api   ajax调用不对外暴露

                                    //第三类url  只供微服直接调用 /inner
    @RequestMapping("/api/item/{skuId}")
    Result<Map<String, Object>> getItem(@PathVariable("skuId") Long skuId);
}
