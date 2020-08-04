package com.att.gmall.product.service.impl;

import com.att.gmall.model.list.SearchAttr;
import com.att.gmall.model.product.BaseAttrInfo;
import com.att.gmall.model.product.BaseAttrValue;
import com.att.gmall.product.mapper.BaseAttrInfoMapper;
import com.att.gmall.product.mapper.BaseAttrValueMapper;
import com.att.gmall.product.service.AttrInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
@Service
public class AttrInfoServiceImpl implements AttrInfoService {
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseAttrInfo> attrInfoList(String category1Id, String category2Id, String category3Id) {
        QueryWrapper<BaseAttrInfo> baseAttrInfoQueryWrapper = new QueryWrapper<>();
        baseAttrInfoQueryWrapper.eq("category_level", 3);
        baseAttrInfoQueryWrapper.eq("category_id", category3Id);

        List<BaseAttrInfo> baseAttrInfos = baseAttrInfoMapper.selectList(baseAttrInfoQueryWrapper);
        for (BaseAttrInfo baseAttrInfo : baseAttrInfos) {
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", baseAttrInfo.getId());
            List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.selectList(queryWrapper);
            baseAttrInfo.setAttrValueList(baseAttrValueList);
        }
        return baseAttrInfos;
    }

    @Override
    @Transactional
    public void saveAttrInfo( BaseAttrInfo baseAttrInfo) {

        Long attrId = baseAttrInfo.getId();
        //有id修改
            if (attrId!=null&&attrId>0){
         baseAttrInfoMapper.updateById(baseAttrInfo);
         //主表修改完把副表附属的删除
                QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
             queryWrapper.eq("att_id", attrId);
                baseAttrValueMapper.delete(queryWrapper);
            }else {
                //没id添加
                baseAttrInfoMapper.insert(baseAttrInfo);
                attrId = baseAttrInfo.getId();
            }

//添加附属表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            if (null!=attrValueList&&attrValueList.size()>0){
                for (BaseAttrValue baseAttrValue : attrValueList) {
                    baseAttrValue.setAttrId(attrId);
                    baseAttrValueMapper.insert(baseAttrValue);
                }
            }

    }

    @Override
    public List<SearchAttr> getAttrList(Long skuId) {

        List<SearchAttr> baseAttrInfos = baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);

        return baseAttrInfos;
    }

}
