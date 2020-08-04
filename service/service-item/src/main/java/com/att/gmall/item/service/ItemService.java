package com.att.gmall.item.service;

import java.util.Map;

public interface ItemService {
    Map<String, Object> getItem(Long skuId);

    Map<String, Object> getItemThread(Long skuId);
}
