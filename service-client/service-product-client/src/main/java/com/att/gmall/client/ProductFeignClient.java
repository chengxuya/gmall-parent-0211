package com.att.gmall.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("service-product")
public interface ProductFeignClient {
}
