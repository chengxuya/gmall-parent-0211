package com.att.gmall.product.controller;

import com.att.gmall.common.result.Result;
import com.att.gmall.model.product.BaseAttrInfo;
import com.att.gmall.model.product.BaseCategory1;
import com.att.gmall.product.service.AttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin/product")
@CrossOrigin
public class AttrInfoController {
    @Autowired
    private AttrInfoService attrInfoService;

    @RequestMapping("attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result attrInfoList(@PathVariable("category1Id") String category1Id
            ,@PathVariable("category2Id") String category2Id
            ,@PathVariable("category3Id") String category3Id){
        List<BaseAttrInfo> baseAttrInfos= attrInfoService.attrInfoList(category1Id,category2Id,category3Id);

        return Result.ok(baseAttrInfos);
    }
    @RequestMapping("saveAttrInfo")
    public Result  saveAttrInfo( @RequestBody BaseAttrInfo baseAttrInfo){
        attrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

}
