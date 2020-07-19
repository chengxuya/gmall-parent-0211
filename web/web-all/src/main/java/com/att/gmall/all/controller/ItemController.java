package com.att.gmall.all.controller;

import com.att.gmall.common.result.Result;
import com.att.gmall.item.client.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController { //第一类url web模块url  这种可以由用户直接访问

    @Autowired
    private ItemFeignClient itemFeignClient;
    @RequestMapping("test")
    public String test(Model model, ModelMap modelMap, Map map){
        modelMap.put("flag", "1");
        model.addAttribute("hello","hello thymeleaf");
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("元素"+i);
        }
        model.addAttribute("list",list);
        model.addAttribute("num",0);

        return "test";//index.html
    }
    /**
     * sku详情页面
     * @param skuId
     * @param model
     * @return
     */
    @RequestMapping("{skuId}.html")
    public String getItem(@PathVariable Long skuId ,Model model){

        //查询分类数据,item查询的是汇总数据,包括分类集合 商品详情 图片集合 销售属性集合等
        Result<Map<String,Object>> result= itemFeignClient.getItem(skuId);

        model.addAllAttributes(result.getData());//返回是一个map集合

        return "test";//index.html
    }


}