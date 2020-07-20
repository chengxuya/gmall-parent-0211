package com.att.gmall.product.service.impl;

import com.att.gmall.model.product.SkuAttrValue;
import com.att.gmall.model.product.SkuImage;
import com.att.gmall.model.product.SkuInfo;
import com.att.gmall.model.product.SkuSaleAttrValue;
import com.att.gmall.product.mapper.SkuAttrValueMapper;
import com.att.gmall.product.mapper.SkuImageMapper;
import com.att.gmall.product.mapper.SkuInfoMapper;
import com.att.gmall.product.mapper.SkuSaleAttrValueMapper;
import com.att.gmall.product.service.SkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        //保存sku基本信息
        skuInfoMapper.insert(skuInfo);
        Long skuId = skuInfo.getId();
        //保存sku图片集合
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
        //保存sku对应平台属性集合
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }
        //保存sku对应销售属性集合
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }
    }

    @Override
    public IPage<SkuInfo> list(Page pageParam) {
        IPage iPage = skuInfoMapper.selectPage(pageParam, null);
        return iPage;

    }

    @Override
    public void onSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(1);
        skuInfoMapper.updateById(skuInfo);
        //将来要调用es插入已经上架的商品
    }

    @Override
    public void cancelSale(Long skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        skuInfo.setIsSale(0);
        skuInfoMapper.updateById(skuInfo);
        //将来要调用es删除已经下架的商品
    }

    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        QueryWrapper<SkuInfo> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("id", skuId);
        SkuInfo skuInfo = skuInfoMapper.selectOne(queryWrapper);

        QueryWrapper<SkuImage> imageQueryWrapper = new QueryWrapper<>();
        imageQueryWrapper.eq("sku_id", skuId);

        List<SkuImage> skuImages = skuImageMapper.selectList(imageQueryWrapper);
        skuInfo.setSkuImageList(skuImages);

        return skuInfo;
    }
}
