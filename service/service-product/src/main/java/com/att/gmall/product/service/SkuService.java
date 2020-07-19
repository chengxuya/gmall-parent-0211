package com.att.gmall.product.service;

import com.att.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface SkuService {
    void saveSkuInfo(SkuInfo skuInfo);

    IPage<SkuInfo> list(Page pageParam);

    void onSale(Long skuId);

    void cancelSale(Long skuId);
}
