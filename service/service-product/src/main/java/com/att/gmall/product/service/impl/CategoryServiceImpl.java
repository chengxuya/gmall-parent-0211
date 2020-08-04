package com.att.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.att.gmall.model.product.BaseCategory1;
import com.att.gmall.model.product.BaseCategory2;
import com.att.gmall.model.product.BaseCategory3;
import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.product.mapper.BaseCategory1Mapper;
import com.att.gmall.product.mapper.BaseCategory2Mapper;
import com.att.gmall.product.mapper.BaseCategory3Mapper;
import com.att.gmall.product.mapper.BaseCategoryViewMapper;
import com.att.gmall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseCategoryViewMapper baseCategoryViewMapper;

    @Override
    public List<BaseCategory1> getCategory1() {
        //查询dao获得category1表的全部数据

        return  baseCategory1Mapper.selectList(null);
    }
    @Override
    public List<BaseCategory2> getCategory2(String category1Id) {


        QueryWrapper<BaseCategory2> baseCategory2QueryWrapper = new QueryWrapper<>();
        baseCategory2QueryWrapper.eq("category1_id",category1Id);
        List<BaseCategory2> baseCategory2s = baseCategory2Mapper.selectList(baseCategory2QueryWrapper);

        return baseCategory2s;
    }
    @Override
    public List<BaseCategory3> getCategory3(String category2Id) {

        QueryWrapper<BaseCategory3> baseCategory3QueryWrapper = new QueryWrapper<>();
        baseCategory3QueryWrapper.eq("category2_id",category2Id);

        List<BaseCategory3> baseCategory3s = baseCategory3Mapper.selectList(baseCategory3QueryWrapper);

        return baseCategory3s;
    }

    @Override
    public BaseCategoryView getCategoryView(Long category3Id) {

        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id", category3Id);
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(queryWrapper);

        return baseCategoryView;
    }

    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list=new ArrayList<>();

        //dao的分类BaseCategoryView集合
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);

                    //long 是一级分类的id
                    Map<Long,List<BaseCategoryView>> c1map=baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
                    Long index= 1l;//页面要用遍历
                    //放入一级分类的jsonobject
        for (Map.Entry<Long, List<BaseCategoryView>> entry1 : c1map.entrySet()) {
            JSONObject c1jsonObject=new JSONObject();
            Long c1Id = entry1.getKey();
            List<BaseCategoryView>   c1CategoryView = entry1.getValue();
            c1jsonObject.put("index", index);
                c1jsonObject.put("categoryId",c1Id);
                c1jsonObject.put("categoryName",c1CategoryView.get(0).getCategory1Name());
                index++;
                //放入二级分类的JSONObject
            List<JSONObject> c2jsonObjectList=new ArrayList<>();
            Map<Long,List<BaseCategoryView>> c2map=c1CategoryView.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            for (Map.Entry<Long, List<BaseCategoryView>> entry2 : c2map.entrySet()) {
                JSONObject c2jsonObject=new JSONObject();
                Long c2Id = entry2.getKey();
                List<BaseCategoryView>   c2CategoryView = entry2.getValue();

                c2jsonObject.put("categoryId",c2Id);
                c2jsonObject.put("categoryName",c2CategoryView.get(0).getCategory2Name());


                //放入三级分类的JSONObject
                List<JSONObject> c3jsonObjectList=new ArrayList<>();
                Map<Long,List<BaseCategoryView>> c3map=c2CategoryView.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory3Id));
                for (Map.Entry<Long, List<BaseCategoryView>> entry3 : c3map.entrySet()) {
                    JSONObject c3jsonObject=new JSONObject();
                    Long c3Id = entry3.getKey();
                    List<BaseCategoryView>   c3CategoryView = entry3.getValue();

                    c3jsonObject.put("categoryId",c3Id);
                    c3jsonObject.put("categoryName",c3CategoryView.get(0).getCategory3Name());


                    //封装三级分类集合
                    c3jsonObjectList.add(c3jsonObject);
                }
                //二级分类放入三级分类集合
                c2jsonObject.put("categoryChild",c3jsonObjectList);

                //封装二级分类集合
                c2jsonObjectList.add(c2jsonObject);
            }
            //一级分类放入二级分类集合
            c1jsonObject.put("categoryChild",c2jsonObjectList);

            //封装一级分类集合
            list.add(c1jsonObject);
        }
        return list;
    }
}
