package com.att.gmall.list.service.impl;

import com.alibaba.fastjson.JSON;
import com.att.gmall.product.client.ProductFeignClient;
import com.att.gmall.list.repository.GoodsRepository;
import com.att.gmall.list.service.ListApiService;
import com.att.gmall.model.list.*;
import com.att.gmall.model.product.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;

import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;


@Service
public class ListApiServiceImpl implements ListApiService {
    @Autowired
    private GoodsRepository elasticsearchRepository;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) throws IOException {

        // dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        //query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", "荣耀");
        boolQueryBuilder.must(matchQueryBuilder);
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", 61);
        boolQueryBuilder.filter(termQueryBuilder);

        // nested
        BoolQueryBuilder attrsBool = new BoolQueryBuilder();
        attrsBool.filter(new TermQueryBuilder("attrs.attrValue", "8GB"));
        attrsBool.filter(new TermQueryBuilder("attrs.attrId", 3));
        attrsBool.must(new MatchQueryBuilder("attrs.attrValue", "8GB"));
        NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", attrsBool, ScoreMode.None);
        boolQueryBuilder.filter(nestedQueryBuilder);

        // 将bool放入语句
        searchSourceBuilder.query(boolQueryBuilder);

        System.out.println("========" + searchSourceBuilder.toString() + "============");

        // 请求命令对象得封装
        String[] indeces = {"goods"};
        SearchRequest searchRequest = new SearchRequest(indeces, searchSourceBuilder);
        //SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    }

    /**
     * 上架商品列表
     *
     * @param skuId
     */
    @Override
    public void upperGoods(Long skuId) {
        //查询skuInfo
        Goods goods = new Goods();

//查询sku对应的平台属性
        List<SearchAttr> searchAttrList = productFeignClient.getAttrList(skuId);

        goods.setAttrs(searchAttrList);

        //查询sku信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        // 查询品牌
        BaseTrademark baseTrademark = productFeignClient.getTrademark(skuInfo.getTmId());
        if (baseTrademark != null) {
            goods.setTmId(skuInfo.getTmId());
            goods.setTmName(baseTrademark.getTmName());
            goods.setTmLogoUrl(baseTrademark.getLogoUrl());

        }

        // 查询分类
        BaseCategoryView baseCategoryView = productFeignClient.getCategoryView(skuInfo.getCategory3Id());
        if (baseCategoryView != null) {
            goods.setCategory1Id(baseCategoryView.getCategory1Id());
            goods.setCategory1Name(baseCategoryView.getCategory1Name());
            goods.setCategory2Id(baseCategoryView.getCategory2Id());
            goods.setCategory2Name(baseCategoryView.getCategory2Name());
            goods.setCategory3Id(baseCategoryView.getCategory3Id());
            goods.setCategory3Name(baseCategoryView.getCategory3Name());
        }

        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setId(skuInfo.getId());
        goods.setTitle(skuInfo.getSkuName());
        goods.setCreateTime(new Date());

        elasticsearchRepository.save(goods);
    }

    /**
     * 下架商品列表
     *
     * @param skuId
     */
    @Override
    public void lowerGoods(Long skuId) {
        elasticsearchRepository.deleteById(skuId);
    }

    @Override
    public void incrHotScore(Long skuId) {
        //更新redis 返回当前分数
        Double hotScore = redisTemplate.opsForZSet().incrementScore("hotScore", skuId, 1);

        //用分数摸10,如果没有余数,则更新es分                                                                                                          数
        if (hotScore % 10 == 0) {
            //调用es更新
            Optional<Goods> optional = elasticsearchRepository.findById(skuId);
            Goods goods = optional.get();
            goods.setHotScore(hotScore.longValue());

            elasticsearchRepository.save(goods);
        }
    }

    /**
     * 返回展示商品封装类
     *
     * @param searchParam
     * @return com.att.gmall.model.list.SearchResponseVo
     */
    @Override
    public SearchResponseVo list(SearchParam searchParam) {
        //拼接dsl的封装方法
        SearchRequest searchRequest = buildQueryDsl(searchParam);

        //执行dsl查询命令
        SearchResponse search = null;

        try {
            search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //封装vo对象方法
        SearchResponseVo searchResponseVo = parseSearchResult(search);

//        searchResponseVo.setTrademarkList();
        return searchResponseVo;
    }

    /**
     * //es语句结果返回抽取封装数据方法
     *
     * @param search
     * @return com.att.gmall.model.list.SearchResponseVo
     */
    private SearchResponseVo parseSearchResult(SearchResponse search) {
        SearchResponseVo searchResponseVo = new SearchResponseVo();
        //解析返回结果

        //封装商品
        List<Goods> goods = new ArrayList<>();
        Set<SearchResponseTmVo> baseTrademarks = new HashSet<>();

        //用来存放去重 所需的List<SearchResponseAttrVo>集合   baseAttrInfos存放 Set<SearchResponseAttrVo>
        Set<SearchResponseAttrVo> baseAttrInfos = new HashSet<>();

        //再建一个只临时存放的一个具体属性值的去重集合      searchAttrSet临时存放  的只存放一个的属性值 集合
        Set<SearchAttr> searchAttrSet = new HashSet<>();

        SearchHits hits = search.getHits();
        SearchHit[] resultHits = hits.getHits();
        if (resultHits != null && resultHits.length > 0) {
            for (SearchHit resultHit : resultHits) {
                String sourceAsString = resultHit.getSourceAsString();
                Goods good = JSON.parseObject(sourceAsString, Goods.class);

                //解析高亮
                Map<String, HighlightField> highlightFields = resultHit.getHighlightFields();
                if (highlightFields!=null&&highlightFields.size()>0){
                    HighlightField title = highlightFields.get("title");
                    String titleName = title.getFragments()[0].toString();
                    good.setTitle(titleName);
                }

                goods.add(good);

                //把品牌放入集合
                SearchResponseTmVo baseTrademark = new SearchResponseTmVo();
                baseTrademark.setTmId(good.getTmId());
                baseTrademark.setTmName(good.getTmName());
                baseTrademark.setTmLogoUrl(good.getTmLogoUrl());
                baseTrademarks.add(baseTrademark);

                //把属性放入集合

                List<SearchAttr> attrs = good.getAttrs();
                //遍历good里面的searchArr集合 获取里面的每一个searchArr的属性值
                for (SearchAttr attr : attrs) {
                    SearchAttr searchAttr = new SearchAttr();
                    searchAttr.setAttrId(attr.getAttrId());
                    searchAttr.setAttrValue(attr.getAttrValue());
                    //存放到一个临时存放的属性值集合
                    searchAttrSet.add(searchAttr);

                    //把good的searchArr 平台属性的id name  放入所需的searchResponseAttrVo里面封装数据
                    SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
                    searchResponseAttrVo.setAttrId(attr.getAttrId());
                    searchResponseAttrVo.setAttrName(attr.getAttrName());
                    baseAttrInfos.add(searchResponseAttrVo);
                }
            }

        }
                     //   从存放searchResponseAttrVo的集合中 遍历数据封装集合属性到searchResponseAttrVo成员属性里面的属性值集合
        for (SearchResponseAttrVo searchResponseAttrVo : baseAttrInfos) {
            for (SearchAttr searchAttr : searchAttrSet) {
                //根据他们的属性id做比较,如果相同则给它的属性值集合进行数据存储封装
                if (searchResponseAttrVo.getAttrId() == searchAttr.getAttrId()) {
                    searchResponseAttrVo.getAttrValueList().add(searchAttr.getAttrValue());
                }
            }
        }

        //商标聚合解析
//        Map<String, Aggregation> stringAggregationMap = search.getAggregations().asMap();
//        ParsedLongTerms tmIdAggParsedLongTerms=(ParsedLongTerms)stringAggregationMap.get("tmIdAgg");
//      //Aggregation tmIdAgg = stringAggregationMap.get("tmIdAgg");
//
//        List<SearchResponseTmVo> trademarkList =tmIdAggParsedLongTerms.getBuckets().stream().map(bucket->{
//            SearchResponseTmVo searchResponseTmVo =new SearchResponseTmVo();
//
//            String keyAsString = bucket.getKeyAsString();
//
//            //跟解析tmId的聚合一样,在进行一次聚合循环,拿到tmName
//            Map<String, Aggregation> tmIdSubMap = bucket.getAggregations().asMap();
//            ParsedLongTerms tmNameAgg=   (ParsedLongTerms)tmIdSubMap.get("tmNameAgg");
//            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
//
//            //跟解析tmLogoUrl的聚合一样,在进行一次聚合循环,拿到tmLogoUrl
//        Map<String, Aggregation> tmLogoUrlSubMap = bucket.getAggregations().asMap();
//
//            ParsedLongTerms tmLogoUrlAgg=   (ParsedLongTerms)tmLogoUrlSubMap.get("tmLogoUrlAgg");
//            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
//
//            searchResponseTmVo.setTmId(Long.parseLong(keyAsString));
//                 searchResponseTmVo.setTmName(tmName);
//                 searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
//            return searchResponseTmVo;
//        }).collect(Collectors.toList());

        // 属性聚合解析
//        ParsedNested attrAgg = (ParsedNested) stringAggregationMap.get("attrsAgg");
//        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
//        List<? extends Terms.Bucket> attrIdBuckets = attrIdAgg.getBuckets();
//
//        List<SearchResponseAttrVo> searchResponseAttrVos = attrIdBuckets.stream().map(attrIdBucket->{
//            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
//            long attrId = attrIdBucket.getKeyAsNumber().longValue();
//            searchResponseAttrVo.setAttrId(attrId);
//
//            Map<String, Aggregation> attrIdSubMap = attrIdBucket.getAggregations().asMap();
//            ParsedStringTerms attrNameAgg = (ParsedStringTerms) attrIdSubMap.get("attrNameAgg");
//            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
//            searchResponseAttrVo.setAttrName(attrName);
//
//            Map<String, Aggregation> attrValueSubMap = attrIdBucket.getAggregations().asMap();
//            ParsedStringTerms attrValueAgg = (ParsedStringTerms) attrValueSubMap.get("attrValueAgg");
//            List<String> attrValues = attrValueAgg.getBuckets().stream().map(attrValueBucket->{
//                String attrValue = attrValueBucket.getKeyAsString();
//                return attrValue;
//            }).collect(Collectors.toList());
//            searchResponseAttrVo.setAttrValueList(attrValues);// 封装属性值的解析结果给属性Vo集合
//            return searchResponseAttrVo;
//        }).collect(Collectors.toList());


        List<SearchResponseTmVo> trademarkList = new ArrayList<>(baseTrademarks);
        //把封装好的SearchResponseAttrVo的去重集合baseAttrInfos放入list给 searchResponseVo前台页面数据使用
        List<SearchResponseAttrVo> searchResponseAttrVoList = new ArrayList<>(baseAttrInfos);

        searchResponseVo.setGoodsList(goods);

//     searchResponseVo.setTrademarkList(baseTrademarkList);
        searchResponseVo.setTrademarkList(trademarkList);

        searchResponseVo.setAttrsList(searchResponseAttrVoList);
//        searchResponseVo.setAttrsList(searchResponseAttrVos);
        //封装商标
        return searchResponseVo;
    }

    /**
     * //抽取拼接es语句方法 构建dsl语句
     *
     * @param searchParam
     * @return org.elasticsearch.action.search.SearchRequest
     */
    private SearchRequest buildQueryDsl(SearchParam searchParam) {
        //拼接dsl的封装
        String[] indeces = {"goods"};
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //分类
        Long category1Id = searchParam.getCategory1Id();
        Long category2Id = searchParam.getCategory2Id();
        Long category3Id = searchParam.getCategory3Id();
        if (category3Id != null && category3Id > 0) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category3Id", category3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //关键字
        String keyword = searchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", keyword);
          //  MatchPhraseQueryBuilder
            boolQueryBuilder.must(matchQueryBuilder);
        }

        // 属性集合
        // 属性id:属性值名称:属性名称
        String[] props = searchParam.getProps();
        if(null!=props&&props.length>0) {
            for (String prop : props) {
                String[] split = prop.split(":");
                String attrId = split[0];
                String attrValue = split[1];
                String attrName = split[2];

                // nested的属性
                BoolQueryBuilder attrsBool = new BoolQueryBuilder();
                attrsBool.filter(new TermQueryBuilder("attrs.attrId", attrId));
                attrsBool.filter(new TermQueryBuilder("attrs.attrValue", attrValue));
                attrsBool.must(new MatchQueryBuilder("attrs.attrName", attrName));
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", attrsBool, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }

        }
        //商标
        String trademark = searchParam.getTrademark();
        if (StringUtils.isNotBlank(trademark)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("tmId",  trademark.split(":")[0]);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);


        //聚合商标结果
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("tmIdAgg").field("tmId")
                .subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName"))
                .subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl"));
        searchSourceBuilder.aggregation(termsAggregationBuilder);



        //聚合属性集合结果

        NestedAggregationBuilder attrsNestedAggregationBuilder = AggregationBuilders.nested(("attrsAgg"), ("attrs"))
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId"))
                .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        searchSourceBuilder.aggregation(attrsNestedAggregationBuilder);


        //分页,第一页
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        // 排序规则
        // 后台拼接：1:hotScore 2:price  前台页面传递：order=2:desc

        //排序
        String order=searchParam.getOrder();
        if (StringUtils.isNotBlank(order)){
            String[] split = order.split(":");
            String fieldFlag = split[0];
            String sortOrder = split[1];


            String field = "hotScore";
            if(fieldFlag.equals("2")){
                field = "price";
            }
            searchSourceBuilder.sort(field, sortOrder.equals("asc") ? SortOrder.ASC:SortOrder.DESC);

        }

        //高亮
        if (StringUtils.isNotBlank(keyword)){
            HighlightBuilder highlightBuilder=new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder'>");
            highlightBuilder.field("title");
            highlightBuilder.postTags("</span>");
         searchSourceBuilder.highlighter(highlightBuilder);

        }


        SearchRequest searchRequest = new SearchRequest(indeces, searchSourceBuilder);

        //打印dsl语句
        System.out.println(searchSourceBuilder.toString());
        return searchRequest;
    }

}
