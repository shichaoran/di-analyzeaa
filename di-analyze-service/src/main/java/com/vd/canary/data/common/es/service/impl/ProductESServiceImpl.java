
package com.vd.canary.data.common.es.service.impl;

import java.io.IOException;
import java.util.*;
import javax.validation.Valid;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.vd.canary.data.api.request.es.*;
import com.vd.canary.data.common.es.helper.ESPageRes;
import com.vd.canary.data.common.es.helper.ElasticsearchUtil;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.ProductESService;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.data.util.DateUtil;
import com.vd.canary.utils.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.springframework.stereotype.Service;

/**
 * 商品 ES 业务逻辑实现类
 */
@Slf4j
@Data
@Service
public class ProductESServiceImpl implements ProductESService {

    // 索引
    private String indexName = "productindex_test";

    //类型
    private String esType = "producttype_test";

    // 创建索引
    public String createIndex() {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            if (ElasticsearchUtil.createIndex(indexName, createIndexMapping(indexName))) {
                return "Create productindex success.";
            } else {
                return "Create productindex failure！";
            }
        } else {
            return "Productindex exist！";
        }
    }

    //新增商品信息
    public String saveProduct(ProductsTO product) throws IOException {
        if (product == null || StringUtils.isEmpty(product.getSkuId())) {
            return "param is null.";
        }
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSON(product).toString());
        String id = ElasticsearchUtil.addData(jsonObject, indexName, product.getSkuId());
        if (StringUtils.isNotBlank(id)) {
            return "SaveProduct success.";
        } else {
            return "SaveProduct failure!";
        }
    }
    //新增商品信息
    public String saveProduct(JSONObject product) throws IOException {
        if (product == null || StringUtils.isEmpty(product.get("skuId").toString())) {
            return "param is null.";
        }
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        JSONObject jsonObject = JSONObject.parseObject(product.toString());
        String id = ElasticsearchUtil.addData(jsonObject, indexName, product.get("skuId").toString());
        if (StringUtils.isNotBlank(id)) {
            return "SaveProduct success.";
        } else {
            return "SaveProduct failure!";
        }
    }
    public String saveProduct(String product,String productId) throws IOException {
        if (productId == null ) {
            return "param is null.";
        }
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        System.out.println(product);
        JSONObject jsonObject = JSONObject.parseObject(product);
        String id = ElasticsearchUtil.addData(jsonObject, indexName, productId);
        if (StringUtils.isNotBlank(id)) {
            return "SaveProduct success.";
        } else {
            return "SaveProduct failure!";
        }
    }

    //新增或修改商品信息
    public void saveOrUpdateProduct(ProductsTO product) throws IOException {
        if (product == null || StringUtils.isEmpty(product.getSkuId())) {
            return;
        }
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        if (ElasticsearchUtil.existById(indexName, product.getSkuId())) {
            Map content = JSONObject.parseObject(JSONObject.toJSONString(product), Map.class);
            ElasticsearchUtil.updateData(content, indexName, product.getSkuId());
            log.info("indexName:{},skuid:{},update product .", indexName, product.getSkuId());
        } else {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSON(product).toString());
            String id = ElasticsearchUtil.addData(jsonObject, indexName, product.getSkuId());
            log.info("indexName:{},skuid:{},save product return id: {}", indexName, product.getSkuId(), id);
        }
    }

    // 批量新增商品信息
    public void batchAddProduct(List<ProductsTO> products) {
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        if (products == null || products.size() == 0) {
            return;
        }
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        Map<String, JSONObject> map = new HashMap<>();
        for (ProductsTO product : products) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSON(product).toString());
            map.put(product.getSkuId(), jsonObject);
        }
        ElasticsearchUtil.insertBatch(indexName, map);
    }

    //删除商品信息
    public void deletedProductById(String id) throws IOException {
        ElasticsearchUtil.deleteById(indexName, id);
    }

    // 根据productId更新信息
    public void updateProduct(ProductsTO product) throws IOException {
        saveOrUpdateProduct(product);
    }
    public void updateProduct(Map<String,Object> map) throws IOException {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        ElasticsearchUtil.updateData(map, indexName, map.get("skuId").toString());
        log.info("indexName:{},skuid:{},update product,map{} .", indexName, map.get("skuId").toString(),map);
    }

    // 通过id获取数据
    public Map<String, Object> findById(String id) throws IOException {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        return ElasticsearchUtil.searchDataById(indexName, id);
    }

    // 通过 skuid 数组列表返回查询结果，不分页
    public List<Map<String, Object>> findByIds(CategoryReq categoryReq) {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        List<Map<String, Object>> result = Lists.newArrayList();
        if(categoryReq == null || categoryReq.getSkuIdList().size() == 0){
            return result;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termsQuery("skuId", categoryReq.getSkuIdList()));
        List<Map<String, Object>> list = ElasticsearchUtil.searchByQuery(indexName,boolQuery);
        return list;
    }

    // 自定义Map参数查找，不分页, 目前只支持全must格式
    public List<Map<String, Object>> boolQueryByCustomMap( Map<String,String> map) {
        List<Map<String, Object>> result = Lists.newArrayList();
        if(map == null ){
            return result;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            boolQuery.must(QueryBuilders.termQuery(entry.getKey(), entry.getValue() ));
        }
        List<Map<String, Object>> list = ElasticsearchUtil.searchByQuery(indexName,boolQuery);
        return list;
    }

    //通过一级类目 二级类目 三级类目 分页搜索数据 分页
    public ESPageRes boolQueryByDiffCategorys(Integer pageNumber, Integer pageSize, @Valid ThreeCategoryReq req) {
        if (req == null || ( req.getFOneCategoryCode()==null && req.getFTwoCategoryCode()==null && req.getFThreeCategoryCode()== null ) ) {
            List<Map<String, Object>> recordList = new ArrayList<>();
            return new ESPageRes(pageNumber, pageSize, 0, recordList);
        }
        if (pageNumber == null || pageNumber < Constant.ES_DEFAULT_PAGE_NUMBER) {
            pageNumber = Constant.ES_DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = Constant.ES_PAGE_SIZE;
        }
        String fields = null;
        String sortField = null;
        String sortTpye = null;
        String highlightField = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if( req.getFOneCategoryCode() != null){
            boolQuery.must(QueryBuilders.termQuery("fOneCategoryCode.keyword", req.getFOneCategoryCode() ));
        }
        if( req.getFTwoCategoryCode() != null){
            boolQuery.must(QueryBuilders.termQuery("fTwoCategoryCode.keyword", req.getFTwoCategoryCode() ));
        }
        if( req.getFThreeCategoryCode() != null){
            boolQuery.must(QueryBuilders.termQuery("fThreeCategoryCode.keyword", req.getFThreeCategoryCode() ));
        }
        boolQuery.must(QueryBuilders.termQuery("shelvesState.keyword", "1" ));
        ESPageRes esPageRes = ElasticsearchUtil.searchDataPage(indexName, pageNumber, pageSize, boolQuery, fields, sortField, sortTpye, highlightField);
        return esPageRes;
    }

    // 商详页数据展示
    public List<Map<String, Object>> boolQueryForProductDetail(ProductDetailsReq req) {
        List<Map<String, Object>> result = Lists.newArrayList();
        if(req == null && req.getStoreId() == null && req.getSpuId() == null ){
            return result;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(req.getStoreId())){
            boolQuery.must(QueryBuilders.termQuery("storeId.keyword", req.getStoreId()));
        }
        if(StringUtils.isNotBlank(req.getSpuId())){
            boolQuery.must(QueryBuilders.termQuery("proSkuSpuId.keyword", req.getSpuId()));
        }
        if(StringUtils.isNotBlank(req.getSkuId())){
            boolQuery.must(QueryBuilders.termQuery("skuId.keyword", req.getSkuId()));
        }
        boolQuery.must(QueryBuilders.termQuery("shelvesState.keyword", "1" ));
        List<Map<String, Object>> list = ElasticsearchUtil.searchByQuery(indexName,boolQuery);
        return list;
    }

    // 商城首页 钢筑采 频道搜索入口
    public ESPageRes boolQueryByKeyword(Integer pageNumber, Integer pageSize, SteelReq req) {
        if (req == null) {
            List<Map<String, Object>> recordList = new ArrayList<>();
            return new ESPageRes(0, 0, 0, recordList);
        }
        if (pageNumber == null || pageNumber < Constant.ES_DEFAULT_PAGE_NUMBER) {
            pageNumber = Constant.ES_DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = Constant.ES_PAGE_SIZE;
        }
        String fields = null;
        String sortField = null;
        String sortTpye = null;
        String highlightField = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (req.getSpecCommands() != null && req.getSpecCommands().size() > 0) {//规格
            boolQuery.must(QueryBuilders.termsQuery("attributeMapJson.attributeName", req.getSpecCommands()));
        }
        if (req.getSpecCommands() != null && req.getSpecCommands().size() > 0) {//规格
            boolQuery.must(QueryBuilders.termsQuery("attributeMapJson.attributeValue.attributeValueName", req.getSpecCommands()));
        }

        if (req.getSpuNames() != null && req.getSpuNames().size() > 0) {//spu名称
            boolQuery.must(QueryBuilders.matchQuery("proSkuSpuName", req.getSpuNames()));
        }
        if (req.getBBrandName() != null && req.getBBrandName().size() > 0) {//品牌
            //boolQuery.must(QueryBuilders.matchQuery("proSkuBrandName", req.getBBrandName()));
            boolQuery.must(QueryBuilders.matchQuery("businessBrand", req.getBBrandName()));
        }
        if (StringUtils.isNotBlank(req.getOneFrontCategory()) ) {//三级分类
            //boolQuery.must(QueryBuilders.matchPhraseQuery("fThreeCategoryName", req.getOneFrontCategory()));
            boolQuery.must(QueryBuilders.matchQuery("fThreeCategoryName", req.getOneFrontCategory()));
        }
        if (StringUtils.isNotBlank(req.getTwoFrontCategory()) ) {//二级分类
            //boolQuery.must(QueryBuilders.matchPhraseQuery("fTwoCategoryName", req.getTwoFrontCategory()));
            boolQuery.must(QueryBuilders.matchQuery("fTwoCategoryName", req.getTwoFrontCategory()));
        }
        if (StringUtils.isNotBlank(req.getThreeFrontCategory()) ) {//一级分类
            //boolQuery.must(QueryBuilders.matchPhraseQuery("fOneCategoryName", req.getThreeFrontCategory()));
            boolQuery.must(QueryBuilders.matchQuery("fOneCategoryName", req.getThreeFrontCategory()));
        }
        if (StringUtils.isNotBlank(req.getSkuRegionalName()) ) { //供货区域
            boolQuery.must(QueryBuilders.matchPhraseQuery("regionalName", req.getSkuRegionalName()));
        }
        if (StringUtils.isNotBlank(req.getPriceSort())) {
            if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(40)){
                sortField = "price";
                sortTpye = req.getPriceSort(); // 商品价格排序
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(50)){
                sortField = "vipPrice";
                sortTpye = req.getPriceSort(); // 商品价格排序
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(60)){
                sortField = "referencePrice";
                sortTpye = req.getPriceSort(); // 商品价格排序
            }
        }
        if (StringUtils.isNotBlank(req.getIsDiscussPrice()) && req.getIsDiscussPrice().equals("1")) {//是否议价 0-包含议价的商品，1-不含议价的商品
            if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(40)){
                boolQuery.must(QueryBuilders.rangeQuery("price").gt(0));
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(50)){
                boolQuery.must(QueryBuilders.rangeQuery("vipPrice").gt(0));
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(60)){
                boolQuery.must(QueryBuilders.rangeQuery("referencePrice").gt(0));
            }
        }
        boolQuery.must(QueryBuilders.termQuery("shelvesState.keyword", "1" ));
        ESPageRes esPageRes = ElasticsearchUtil.searchDataPage(indexName, pageNumber, pageSize, boolQuery, fields, sortField, sortTpye, highlightField);
        return esPageRes;
    }

    // 关键菜搜索时是否能精准匹配到店铺
    public List<Map<String, Object>> boolQueryByExistsShopKeyword(ProductsReq req) {
        if(req == null || req.getKey() == null){
            return null;
        }
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(req.getKey())){
            //boolQuery.must(QueryBuilders.wildcardQuery("storeName", "*"+req.getKey()+"*"));
            //boolQuery.must(QueryBuilders.matchQuery("storeName", "*"+req.getKey()+"*" ));
            boolQuery.must(QueryBuilders.matchPhraseQuery("storeName", req.getKey() ));
        }
        boolQuery.must(QueryBuilders.termQuery("shelvesState.keyword", "1" ));
        List<Map<String, Object>> list = ElasticsearchUtil.searchByQuery(indexName,boolQuery);
        return list;
    }

    // 通过指定店铺搜索该店铺下所有商品 分页
    public ESPageRes allProductByStoreId(Integer pageNumber, Integer pageSize, String stroreId) {
        if (stroreId == null) {
            List<Map<String, Object>> recordList = new ArrayList<>();
            return new ESPageRes(0, 0, 0, recordList);
        }
        if (pageNumber == null || pageNumber < Constant.ES_DEFAULT_PAGE_NUMBER) {
            pageNumber = Constant.ES_DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = Constant.ES_PAGE_SIZE;
        }
        String fields = null;
        String sortField = null;
        String sortTpye = null;
        String highlightField = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        boolQuery.mustNot(QueryBuilders.termQuery("storeId",stroreId));
        boolQuery.must(QueryBuilders.termQuery("shelvesState.keyword", "1" ));
        ESPageRes esPageRes = ElasticsearchUtil.searchDataPage(indexName, pageNumber, pageSize, boolQuery, fields, sortField, sortTpye, highlightField);
        return esPageRes;

    }

    /**
     * 功能：首页顶部商品搜索框通过 关键字分词查询  支持 高亮 排序 并分页
     * 使用QueryBuilders
     * .termQuery("key", obj) 完全匹配
     * .termsQuery("key", obj1, obj2..)   一次匹配多个值
     * .matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
     * .multiMatchQuery("text", "field1", "field2"..);  匹配多个字段, field有通配符忒行
     * .matchAllQuery();         匹配所有文件
     * .termQuery(key+".keyword",value) 精准查找
     * 组合查询 QueryBuilders.boolQuery()
     * .must(QueryBuilders) :   AND
     * .mustNot(QueryBuilders): NOT
     * .should:                  : OR
     */
    public ESPageRes boolQueryByKeyword(Integer pageNumber, Integer pageSize, ProductsReq req) {
        if (pageNumber == null || pageNumber < Constant.ES_DEFAULT_PAGE_NUMBER) {
            pageNumber = Constant.ES_DEFAULT_PAGE_NUMBER;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = Constant.ES_PAGE_SIZE;
        }
        String fields = null;
        String sortField = null;
        String sortTpye = null;
        String highlightField = null;
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(req.getKey())) {// keyword 关键字搜索
            String escapeKey = QueryParser.escape(req.getKey());
            /*boolQuery.must(QueryBuilders.multiMatchQuery(escapeKey,
                                                         "proSkuSpuName", "proSkuSkuName", "proSkuTitle", "proSkuSubTitle",
                                                         "threeCategoryName", "bBrandName", "brandShorthand").fuzziness(Fuzziness
                                                         .AUTO));*/
            boolQuery.must(QueryBuilders.multiMatchQuery(escapeKey,
                                                         "proSkuSpuName", "proSkuSkuName", "proSkuTitle", "proSkuSubTitle",
                                                         "fThreeCategoryName", "bBrandName", "brandShorthand") );
        }
        if (req.getProductBrandName() != null && req.getProductBrandName().size() > 0) {//品牌id 列表
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for(String brandName : req.getProductBrandName() ){
                //query.should(QueryBuilders.boolQuery().filter(QueryBuilders.matchPhraseQuery("bBrandName",brandName)));
                query.should(QueryBuilders.matchQuery("bBrandName",brandName));
            }
            boolQuery.must(query);
        }
        if (req.getFrontThreeCategoryName() != null && req.getFrontThreeCategoryName().size() > 0) {//后台三级分类id 列表
            //boolQuery.must(QueryBuilders.matchPhraseQuery("fThreeCategoryName", req.getFrontThreeCategoryName()));
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for(String categoryName : req.getFrontThreeCategoryName() ){
                query.should(QueryBuilders.matchQuery("fThreeCategoryName",categoryName));
            }
            boolQuery.must(query);
        }
        if (req.getBusinessAreaName() != null && req.getBusinessAreaName().size() > 0) { //供货区域id 列表
            //boolQuery.must(QueryBuilders.matchPhraseQuery("skuRegionalName", req.getBusinessAreaName()));
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for(String areaName : req.getBusinessAreaName() ){
                query.should(QueryBuilders.matchQuery("skuRegionalName",areaName));
            }
            boolQuery.must(query);
        }
        if (StringUtils.isNotBlank(req.getPriceSort())) {
            if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(40)){
                sortField = "price";
                sortTpye = req.getPriceSort(); // 商品价格排序
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(50)){
                sortField = "vipPrice";
                sortTpye = req.getPriceSort(); // 商品价格排序
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(60)){
                sortField = "referencePrice";
                sortTpye = req.getPriceSort(); // 商品价格排序
            }
        }
        if (StringUtils.isNotBlank(req.getIsDiscussPrice()) && req.getIsDiscussPrice().equals("1")) {//是否议价 0-包含议价的商品，1-不含议价的商品
            if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(40)){
                boolQuery.must(QueryBuilders.rangeQuery("price").gt(0));
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(50)){
                boolQuery.must(QueryBuilders.rangeQuery("vipPrice").gt(0));
            }else if(StringUtils.isNotBlank(req.getMemberLevel()) && req.getMemberLevel().equals(60)){
                boolQuery.must(QueryBuilders.rangeQuery("referencePrice").gt(0));
            }
        }
        if (StringUtils.isNotBlank(req.getIsHaveHouse()) && req.getIsHaveHouse().equals("1")) {//是否入驻展厅 是否入驻 0-全部商品，1-入驻展厅的商品
            boolQuery.must(QueryBuilders.matchQuery("boothBusinessBoothCode","[*"));
        }
        boolQuery.must(QueryBuilders.termQuery("shelvesState.keyword", "1" ));
        /*if(StringUtils.isEmpty(req.getKey())){
            QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
            ESPageRes esPageRes = ElasticsearchUtil.searchDataPage(indexName, pageNumber, pageSize, queryBuilder, fields, sortField, sortTpye, highlightField);
            return esPageRes;
        }else{
            ESPageRes esPageRes = ElasticsearchUtil.searchDataPage(indexName, pageNumber, pageSize, boolQuery, fields, sortField, sortTpye, highlightField);
            return esPageRes;
        }*/
        ESPageRes esPageRes = ElasticsearchUtil.searchDataPage(indexName, pageNumber, pageSize, boolQuery, fields, sortField, sortTpye, highlightField);
        return esPageRes;

    }

    /**
     * index mapping
     * 说明：xx.startObject("m_id").field("type","keyword").endObject().field("type", "date")
     * .field("format", "yyyy-MM")  //m_id:字段名,type:文本类型,analyzer 分词器类型
     * 该字段添加的内容，查询时将会使用ik_max_word 分词 //ik_smart  ik_max_word  standard
     * 创建索引有三种方式：1、HTTP的方式创建的列子；2、Map创建的方式；3、使用Builder的方式；
     */
    private XContentBuilder createIndexMapping(String indexName){
        // 方式三：使用XContentBuilder
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject(indexName+"_type");
                {
                    builder.startObject("properties");
                    {
                        builder.startObject("skuId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSkuBrandId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSkuSpuId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSkuSpuCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSkuSpuName"); { builder.field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart"); }
                        builder.endObject();
                        builder.startObject("proSkuSkuCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSkuSkuName"); { builder.field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart"); }
                        builder.endObject();
                        builder.startObject("proSkuTitle"); { builder.field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart"); }
                        builder.endObject();
                        builder.startObject("proSkuSubTitle"); { builder.field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart"); }
                        builder.endObject();
                        //builder.startObject("threeCategoryId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("threeCategoryCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("threeCategoryName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuSupplierId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuSupplierName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuState"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSkuSkuPicJson"); { builder.field("type", "nested"); } // keyword
                        builder.endObject();
                        builder.startObject("skuValuationUnit"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuIntroduce"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuAuxiliaryUnit"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuGmtCreateTime"); { builder.field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss"); }
                        builder.endObject();
                        builder.startObject("skuGmtModifyTime"); { builder.field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss"); }
                        builder.endObject();
                        builder.startObject("spuState"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("proSpuSpuPic"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("spuTitle"); { builder.field("type", "text").field("analyzer", "ik_max_word").field("search_analyzer", "ik_smart"); }
                        builder.endObject();
                        //builder.startObject("attributeCode"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        //builder.startObject("attributeName"); { builder.field("type", "text").field("analyzer", "ik_max_word")
                        // .field("search_analyzer", "ik_smart"); }
                        //builder.endObject();
                        //builder.startObject("value_Name"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        //builder.startObject("attributeId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        //builder.startObject("attributeValueId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("attributeMap"); { builder.field("type", "nested"); } // object
                        builder.endObject();
                        //builder.startObject("attributeType"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        //builder.startObject("oneCategoryId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("oneCategoryCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("oneCategoryName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        //builder.startObject("twoCategoryId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("twoCategoryCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("twoCategoryName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("brandCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("bBrandName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("brandLoge"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("brandShorthand"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("brandIntroduction"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        //builder.startObject("fOneCategoryId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("fOneCategoryCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("fOneCategoryName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        //builder.startObject("fTwoCategoryId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("fTwoCategoryCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("fTwoCategoryName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        //builder.startObject("fThreeCategoryId"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("fThreeCategoryCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("fThreeCategoryName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        //builder.startObject("type"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        //builder.startObject("fileUrl"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        //builder.startObject("fileSortNumber"); { builder.field("type", "keyword"); }
                        //builder.endObject();
                        builder.startObject("regionalCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("regionalName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("regionalScope"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuSellPriceJson"); { builder.field("type", "nested"); } // keyword
                        builder.endObject();
                        builder.startObject("skuSellPriceType"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("warehouseId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("warehouseName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("inventory"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("regionalId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("skuRegionalName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("storeId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("categoryId"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("storeName"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("businessCategory"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("mainProducts"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("businessArea"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("boothBusinessBoothCode"); { builder.field("type", "nested"); }
                        builder.endObject();
                        builder.startObject("customerProfilesLevel"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("approveState"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("enterpriseType"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("storeInfoStoreQrCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("gmtCreateTime"); { builder.field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss"); }
                        builder.endObject();
                        builder.startObject("boothScheduledTime"); { builder.field("type", "date").field("format", "yyyy-MM-dd HH:mm:ss"); }
                        builder.endObject();
                        builder.startObject("warehouseCode"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("warehouseType"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("warehouseRegional"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("detailedAddress"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("spuAttributeMap"); { builder.field("type", "nested"); } // keyword
                        builder.endObject();
                        builder.startObject("isGmProduct"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("shelvesState"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("price"); { builder.field("type", "double"); }
                        builder.endObject();
                        builder.startObject("vipPrice"); { builder.field("type", "double"); }
                        builder.endObject();
                        builder.startObject("referencePrice"); { builder.field("type", "double"); }
                        builder.endObject();
                        builder.startObject("remark1"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("remark2"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("remark3"); { builder.field("type", "keyword"); }
                        builder.endObject();
                        builder.startObject("remark4"); { builder.field("type", "keyword"); }
                        builder.endObject();
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }


    // start 测试索引productindex1 专用
    public String testAddData(String c) throws IOException {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        Map<String,Object> map = new HashMap();
        map.put("id","00b6d5612f734414d68088e359e4b008");// 主键id
        map.put("user_name", "zhangsan");// 用户名
        map.put("user_password", "123456");// 密码
        map.put("user_real_name", "张三");// 用户真实姓名
        //map.put("create_time", DateUtil.getCurrentTimeStr());// 创建时间
        map.put("is_deleted", 0);// 是否删除
        JSONObject jsonObject= JSONObject.parseObject(JSON.toJSONString(map));
        String id = ElasticsearchUtil.addData(jsonObject, indexName, "00b6d5612f734414d68088e359e4b008");
        if (StringUtils.isNotBlank(id)) {
            return "SaveProduct success.";
        } else {
            return "SaveProduct failure!";
        }
    }
    public String insertBatch(String c) throws IOException {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        Map<String ,JSONObject> mapJSON = new HashMap<>();
        Map<String,Object> map = new HashMap();
        map.put("id","00b6d5612f734414d68088e359e4b009");// 主键id
        map.put("user_name", "lisi");// 用户名
        map.put("user_password", "123457");// 密码
        map.put("user_real_name", "李四");// 用户真实姓名
        map.put("create_time", DateUtil.getCurrentTimeStr());// 创建时间
        map.put("is_deleted", 0);// 是否删除
        JSONObject jsonObject= JSONObject.parseObject(JSON.toJSONString(map));
        mapJSON.put("00b6d5612f734414d68088e359e4b009",jsonObject);
        Map<String,Object> map1 = new HashMap();
        map1.put("id","00b6d5612f734414d68088e359e4b010");// 主键id
        map1.put("user_name", "wangwu");// 用户名
        map1.put("user_password", "123458");// 密码
        map1.put("user_real_name", "王五");// 用户真实姓名
        map1.put("create_time", DateUtil.getCurrentTimeStr());// 创建时间
        map1.put("is_deleted", 1);// 是否删除
        JSONObject jsonObject1= JSONObject.parseObject(JSON.toJSONString(map1));
        mapJSON.put("00b6d5612f734414d68088e359e4b010",jsonObject1);
        ElasticsearchUtil.insertBatch(indexName,mapJSON);
        return null;
    }
    public String updateOne(String c) throws IOException {
        Map<String, Object> content = new HashMap();
        //content.put("skuSellPriceType", JSONUtil.toJSONString("0"));
        JSONArray array = JSONArray.parseArray("[{\"attributeId\":\"1251815435083468801\","
                                               + "\"attributeValue\":\"[{\\\"attributeValueId\\\":\\\"1251815331110866945\\\",\\\"attributeValueName\\\":\\\"属性1\\\"}]\",\"attributeType\":\"1\",\"attributeName\":\"属性单选框202004191810\"},{\"attributeId\":\"1251815331068923906\",\"attributeValue\":\"[{\\\"attributeValueId\\\":\\\"1251815331110866945\\\",\\\"attributeValueName\\\":\\\"属性1\\\"}]\",\"attributeType\":\"0\",\"attributeName\":\"属性单选框202004191810\"}]");
        content.put("attributeMap", JSONUtil.toJSONString(array));
        String id = ElasticsearchUtil.updateData(content, indexName, "1251816454693605377");
        if (StringUtils.isNotBlank(id)) {
            return "SaveProduct success.";
        } else {
            return "SaveProduct failure!";
        }
    }
    public String updateAll(String c) throws IOException {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        List<Map<String, Object>> objs = ElasticsearchUtil.selectAll(indexName);
        for(Map<String, Object> content : objs){
            JSONArray array = JSONArray.parseArray("[{\"num\": \"12\", \"price\": \"12\", \"referencePrice\": \"12\",\"vipPrice\": \"12\" }]");
            content.put("skuSellPriceJson", JSONUtil.toJSONString(array));
            String id = ElasticsearchUtil.updateData(content, indexName, content.get("id").toString());
        }
        return "SaveProduct success.";
    }
    public String deleteOne(String c) throws IOException {
        if (!ElasticsearchUtil.isIndexExist(indexName)) {
            ElasticsearchUtil.createIndex(indexName, createIndexMapping( indexName));
        }
        ElasticsearchUtil.deleteById( indexName, "1252565703047811073");
        ElasticsearchUtil.deleteById( indexName, "1252780591888269313");
        return  "Succ";
    }

    // end 测试索引productindex1 专用


}