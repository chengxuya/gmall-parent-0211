package com.att.gmall.product.service;

import com.att.gmall.model.list.SearchAttr;
import com.att.gmall.model.product.BaseAttrInfo;

import java.util.List;

public interface AttrInfoService {
    List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    List<SearchAttr> getAttrList(Long skuId);
}
