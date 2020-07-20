package com.att.gmall.product.service.impl;

import com.att.gmall.model.product.*;
import com.att.gmall.product.mapper.*;
import com.att.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private  BaseTrademarkMapper baseTrademarkMapper;

    @Override
    public IPage<SpuInfo> index(Page pageParam, SpuInfo spuInfo) {
        QueryWrapper<SpuInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", spuInfo.getCategory3Id());
        IPage<SpuInfo> infoIPage = spuInfoMapper.selectPage(pageParam, queryWrapper);

        return infoIPage;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {

        return baseSaleAttrMapper.selectList(null);
    }

    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //        spuInfo 商品表   保存信息生成主键
        spuInfoMapper.insert(spuInfo);
        Long spuId=spuInfo.getId();

        //保存spu销售属性

        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if (spuSaleAttrList != null && spuSaleAttrList.size() >0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuId);
                spuSaleAttrMapper.insert(spuSaleAttr);

                //保存spu销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size() >0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId(spuId);
                        spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
                        spuSaleAttrValueMapper.insert(spuSaleAttrValue);
                    }
                }
            }
        }
        //        spuImage 商品图片表
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if (spuImageList != null && spuImageList.size() >0) {
        for (SpuImage spuImage : spuImageList) {
                    spuImage.setSpuId(spuId);

            spuImageMapper.insert(spuImage);
        }
        }
    }

    @Override
    public List<SpuSaleAttr> spuSaleAttrList(Long spuId) {
        QueryWrapper<SpuSaleAttr> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("spu_id", spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectList(queryWrapper);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            QueryWrapper<SpuSaleAttrValue> queryWrapperForValue = new QueryWrapper<>();
            queryWrapperForValue.eq("spu_id",spuId);
            queryWrapperForValue.eq("base_sale_attr_id",spuSaleAttr.getBaseSaleAttrId());
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.selectList(queryWrapperForValue);
                    spuSaleAttr.setSpuSaleAttrValueList(spuSaleAttrValueList);
        }
        return spuSaleAttrs;
    }

    @Override
    public List<SpuImage> spuImageList(Long spuId) {
        QueryWrapper<SpuImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spu_id", spuId);
        List<SpuImage> spuImageList = spuImageMapper.selectList(queryWrapper);

        return spuImageList;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        List<SpuSaleAttr> spuSaleAttrList=  spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId,spuId);
        return spuSaleAttrList;
    }
}
