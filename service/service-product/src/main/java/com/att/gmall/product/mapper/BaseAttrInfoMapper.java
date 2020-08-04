package com.att.gmall.product.mapper;

import com.att.gmall.model.list.SearchAttr;
import com.att.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseAttrInfoMapper  extends BaseMapper<BaseAttrInfo> {
    List<SearchAttr> selectBaseAttrInfoListBySkuId(Long skuId);
}
