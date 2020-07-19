package com.att.gmall.product.controller;

import com.att.gmall.common.result.Result;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class SkuController {
    @Autowired
    private SkuService skuService;

    @RequestMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
// 调用服务层
        skuService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @RequestMapping("list/{page}/{size}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long size) {
        Page<SkuInfo> pageParam = new Page(page, size);
        IPage<SkuInfo> infoIPage = skuService.list(pageParam);
        return Result.ok(infoIPage);
    }

    @RequestMapping("onSale/{skuId}")
    public Result onSale(@PathVariable Long skuId) {
// 调用服务层
        skuService.onSale(skuId);
        return Result.ok();
    }

    @RequestMapping("cancelSale/{skuId}")
    public Result cancelSale(@PathVariable Long skuId) {
// 调用服务层
        skuService.cancelSale(skuId);
        return Result.ok();
    }
}