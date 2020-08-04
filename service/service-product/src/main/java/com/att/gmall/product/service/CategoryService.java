package com.att.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.att.gmall.model.product.BaseCategory1;
import com.att.gmall.model.product.BaseCategory2;
import com.att.gmall.model.product.BaseCategory3;
import com.att.gmall.model.product.BaseCategoryView;

import java.util.List;

public interface CategoryService {
    List<BaseCategory1> getCategory1();
    List<BaseCategory2> getCategory2(String category1Id);
    List<BaseCategory3> getCategory3(String category2Id);

    BaseCategoryView getCategoryView(Long category3Id);

    List<JSONObject> getBaseCategoryList();
}
