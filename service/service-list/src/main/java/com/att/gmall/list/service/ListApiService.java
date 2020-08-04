package com.att.gmall.list.service;

import com.att.gmall.model.list.SearchParam;
import com.att.gmall.model.list.SearchResponseVo;

public interface ListApiService {
    void upperGoods(Long skuId);

    void lowerGoods(Long skuId);

    void incrHotScore(Long skuId);

    SearchResponseVo list(SearchParam searchParam);
}
