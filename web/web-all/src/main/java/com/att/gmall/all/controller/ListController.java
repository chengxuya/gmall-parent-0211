package com.att.gmall.all.controller;

import com.att.gmall.product.client.ProductFeignClient;
import com.att.gmall.common.result.Result;
import com.att.gmall.list.client.ListFeignClient;
import com.att.gmall.model.list.SearchAttr;
import com.att.gmall.model.list.SearchParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ListController {

    @Autowired
    private ListFeignClient listFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;
    @GetMapping({"/","index"})
    public String index(HttpServletRequest request,SearchParam searchParam, Model model){
        String userId=request.getHeader("userId");
        //userid微服间传递的两种方式 方法一  service到service   方法二 web到service
        //方法一:userid可以将参数写入方法  通过传参方式在feign的方法通过微服之间传递
        //方法二:但是现在是用拦截器的方式把请求头拦截,赋值header到内部的request,,
        // 因为协议不一样,微服间传递会丢失请求头,
               Result result=  productFeignClient.getBaseCategoryList();
                model.addAttribute("list", result.getData());
        return "index/index";
    }
    /**
     * 列表搜索
     * @param searchParam
     * @return
     */
    @GetMapping({"search.html","list.html"})
    public String list(SearchParam searchParam, Model model) {
        Result<Map> result = listFeignClient.list(searchParam);
        model.addAllAttributes(result.getData());

        model.addAttribute("urlParam",makeUrlParam(searchParam));

        //排序
        if (StringUtils.isNotBlank(searchParam.getOrder())){
            //用户使用了排序按钮,记录用户排序规则,返回个页面
            String[] split =searchParam.getOrder().split(":");
            String fieldFlag = split[0];
            String sortOrder = split[1];

            HashMap<String, String> orderMap = new HashMap<>();
            orderMap.put("sort",sortOrder);
            orderMap.put("type", fieldFlag);
        model.addAttribute("orderMap", orderMap);
        }

        //面包屑
        if(StringUtils.isNotBlank(searchParam.getTrademark())){
            model.addAttribute("trademarkParam",searchParam.getTrademark().split(":")[1]);
        }
        String[] props = searchParam.getProps();
        if (props!=null&&props.length>0){
            List<SearchAttr> searchAttrList = new ArrayList<SearchAttr>();
            for (String prop : props) {
                SearchAttr searchAttr = new SearchAttr();
                searchAttr.setAttrId(Long.parseLong(prop.split(":")[0]));
                searchAttr.setAttrValue(prop.split(":")[1]);
                searchAttr.setAttrName(prop.split(":")[2]);
                searchAttrList.add(searchAttr);
            }
            model.addAttribute("propsParamList",searchAttrList);

        }

        return "list/index";
    }

    private String makeUrlParam(SearchParam searchParam) {

        String urlParam = "http://list.gmall.com:8300/search.html?";

        String trademark = searchParam.getTrademark();
        Long category3Id = searchParam.getCategory3Id();
        String keyword = searchParam.getKeyword();
        String[] props = searchParam.getProps();
        String order = searchParam.getOrder();

        if(null!=category3Id&&category3Id>0){
            urlParam += "category3Id="+category3Id;
        }

        if(StringUtils.isNotBlank(keyword)){
            urlParam += "keyword="+keyword;
        }

        if(StringUtils.isNotBlank(trademark)){
            urlParam += "&trademark="+trademark;
        }

        if(null!=props&&props.length>0){
            for (String prop : props) {
                urlParam += "&props="+prop;
            }
        }

        return urlParam;
    }

}