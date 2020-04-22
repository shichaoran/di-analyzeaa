package com.vd.canary.data.service.es.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.CategoryReq;
import com.vd.canary.data.api.request.es.ProductDetailsReq;
import com.vd.canary.data.api.request.es.ProductsReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.CategoryRes;
import com.vd.canary.data.api.response.es.ProductDetailsRes;
import com.vd.canary.data.api.response.es.vo.CategoryVO;
import com.vd.canary.data.common.es.helper.ESPageRes;
import com.vd.canary.data.api.response.es.ProductsRes;
import com.vd.canary.data.api.response.es.vo.ProductsDetailRes;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.service.es.ProductsService;
import com.vd.canary.utils.CollectionUtil;
import com.vd.canary.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
@Service
public class ProductsServiceImpl implements ProductsService {

    @Autowired
    private ProductESServiceImpl productESServiceImpl;

    @Override
    public ResponseBO<ProductsRes> getProductsByKey(@Valid ProductsReq productsReq) throws Exception {
        ResponseBO<ProductsRes> res = new ResponseBO<ProductsRes>();
        ProductsRes productsRes = new ProductsRes();
        log.info("getProductsByKey,productsReq:" + JSONObject.toJSON(productsReq).toString());
        ESPageRes esPageRes = productESServiceImpl.boolQueryByKeyword(productsReq.getPageNum(), productsReq.getPageSize(), productsReq);
        if (esPageRes!=null) {
            List<Map<String, Object>> recordList = esPageRes.getRecordList();
            if (recordList != null && recordList.size() > 0) {
                Map<String, String> categorys = new HashMap<>();//fThreeCategoryId:fThreeCategoryName
                Map<String, String> brands = new HashMap<>(); //proSkuBrandId:bBrandName
                Map<String, Map<String, String>> attributes = new HashMap<>(); //属性
                List<ProductsDetailRes> productDetailResList = new ArrayList<>(); //商品详细列表
                for (Map<String, Object> recordMap : recordList) {
                    ProductsDetailRes productsDetailRes = new ProductsDetailRes();
                    productsDetailRes.setSkuId(recordMap.get("skuId").toString());
                    if(recordMap.containsKey("proSkuTitle") && recordMap.get("proSkuTitle") != null) productsDetailRes.setProSkuTitle(recordMap.get("proSkuTitle").toString() );

                    if(recordMap.containsKey("proSkuSubTitle") && recordMap.get("proSkuSubTitle") != null ) productsDetailRes.setProSkuSubTitle(recordMap.get("proSkuSubTitle").toString() );

                    if(recordMap.containsKey("proSkuSkuPicJson") && recordMap.get("proSkuSkuPicJson") != null) productsDetailRes.setProSkuSkuPicJson(recordMap.get("proSkuSkuPicJson").toString());

                    if(recordMap.containsKey("skuSellPriceJson") && recordMap.get("skuSellPriceJson") != null ){
                        JSONArray array = JSONObject.parseArray(recordMap.get("skuSellPriceJson").toString());
                        productsDetailRes.setSkuSellPriceJson(recordMap.get("skuSellPriceJson").toString());
                    }

                    if (recordMap.containsKey("skuGmtCreateTime") && recordMap.get("skuGmtCreateTime") != null) {
                        LocalDateTime t = LocalDateTime.parse(recordMap.get("skuGmtCreateTime").toString(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        productsDetailRes.setSkuGmtCreateTime(t);
                    }

                    if(recordMap.containsKey("storeId") && recordMap.get("storeId") != null) productsDetailRes.setShopId(recordMap.get("storeId").toString());

                    if(recordMap.containsKey("storeName") && recordMap.get("storeName") != null) productsDetailRes.setStoreInfoName(recordMap.get("storeName").toString() );

                    if(recordMap.containsKey("businessCategory") && recordMap.get("businessCategory") != null ) productsDetailRes.setBusinessCategory(recordMap.get("businessCategory").toString() );

                    if(recordMap.containsKey("mainProducts") && recordMap.get("mainProducts") != null) productsDetailRes.setMainProducts(recordMap.get("mainProducts").toString());

                    if(recordMap.containsKey("businessArea") && recordMap.get("businessArea") != null) productsDetailRes.setBusinessArea(recordMap.get("businessArea").toString());

                    if(recordMap.containsKey("boothBusinessBoothCode") && recordMap.get("boothBusinessBoothCode") != null)productsDetailRes.setBoothBusinessBoothCode(recordMap.get("boothBusinessBoothCode").toString());

                    if(recordMap.containsKey("customerProfilesLevel") && recordMap.get("customerProfilesLevel") != null) productsDetailRes.setCustomerProfilesLevel(recordMap.get("customerProfilesLevel").toString());

                    if(recordMap.containsKey("approveState") && recordMap.get("approveState") != null) productsDetailRes.setApproveState(recordMap.get("approveState").toString());

                    if(recordMap.containsKey("enterpriseType") && recordMap.get("enterpriseType") != null ) productsDetailRes.setEnterpriseType(recordMap.get("enterpriseType").toString());

                    if(recordMap.containsKey("storeInfoStoreQrCode") && recordMap.get("storeInfoStoreQrCode") != null) productsDetailRes.setStoreInfoStoreQrCode(recordMap.get("storeInfoStoreQrCode").toString());

                    if(recordMap.containsKey("gmtCreateTime") && recordMap.get("gmtCreateTime") !=null) {
                        LocalDateTime t = LocalDateTime.parse(recordMap.get("gmtCreateTime").toString(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        productsDetailRes.setGmtCreateTime(t);
                    }

                    if(recordMap.containsKey("regionalId") && recordMap.get("regionalId") !=null) productsDetailRes.setRegionalIdJson(recordMap.get("regionalId").toString());

                    if(recordMap.containsKey("skuRegionalName") && recordMap.get("skuRegionalName") !=null) productsDetailRes.setSkuRegionalNameJson(recordMap.get("skuRegionalName").toString());

                    if(recordMap.containsKey("attributeMap") && recordMap.get("attributeMap") !=null) productsDetailRes.setAttributeMapJson(recordMap.get("attributeMap").toString());

                    productDetailResList.add(productsDetailRes);

                    categorys.put(recordMap.containsKey("fThreeCategoryCode") ? recordMap.get("fThreeCategoryCode").toString() : "", recordMap.containsKey("fThreeCategoryName") ? recordMap.get("fThreeCategoryName").toString() : "");
                    brands.put(recordMap.containsKey("proSkuBrandId") ? recordMap.get("proSkuBrandId").toString() : "", recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");
                    if (attributes.containsKey(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "")) {
                        Map<String, String> mapt = attributes.get(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "");
                        mapt.put(recordMap.containsKey("attributeValueId") ? recordMap.get("attributeValueId").toString() : "", recordMap.containsKey("value_Name") ? recordMap.get("value_Name").toString() : "");
                    } else {
                        Map<String, String> mapt = new HashMap<>();
                        mapt.put(recordMap.containsKey("attributeValueId") ? recordMap.get("attributeValueId").toString() : "", recordMap.containsKey("value_Name") ? recordMap.get("value_Name").toString() : "");
                        attributes.put(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "", mapt);
                    }
                    productsDetailRes.setSkuAuxiliaryUnit(recordMap.containsKey("skuAuxiliaryUnit") ? recordMap.get("skuAuxiliaryUnit").toString() : "");
                    productsDetailRes.setSkuName(recordMap.containsKey("proSkuSkuName")?recordMap.get("proSkuSkuName").toString() : "");
                }
                productsRes.setCategorys(categorys);
                productsRes.setBrands(brands);
                productsRes.setAttributes(attributes);
                productsRes.setProductDetailRes(productDetailResList);
                productsRes.setTotal(esPageRes.getRecordCount());
            }
        }
        res.setData(productsRes);
        res.setSuccess(true);
        res.setCode(200);
        res.setMessage("success.");
        return res;
    }

    //@Override
    public ResponseBO<ProductsRes> getProductByCategory1(@Valid ThreeCategoryReq threeCategoryReq) throws Exception {
        ProductsReq productsReq = new ProductsReq();
        return getProductsByKey(productsReq);
    }
    @Override
    public ResponseBO<ProductsRes> getProductByCategory(@Valid ThreeCategoryReq threeCategoryReq) throws Exception {
        ResponseBO<ProductsRes> res = new ResponseBO<ProductsRes>();
        ProductsRes productsRes = new ProductsRes();
        log.info("getProductByCategory,threeCategoryReq:"+JSONObject.toJSON(threeCategoryReq).toString());
        ESPageRes esPageRes = productESServiceImpl.boolQueryByDiffCategorys(threeCategoryReq.getPageNum(), threeCategoryReq.getPageSize(), new ThreeCategoryReq());
        if (esPageRes!=null) {
            List<Map<String, Object>> recordList = esPageRes.getRecordList();
            if (recordList != null && recordList.size() > 0) {
                Map<String, String> categorys = new HashMap<>();//fThreeCategoryId:fThreeCategoryName
                Map<String, String> brands = new HashMap<>(); //proSkuBrandId:bBrandName
                Map<String, Map<String, String>> attributes = new HashMap<>(); //属性
                List<ProductsDetailRes> productDetailResList = new ArrayList<>(); //商品详细列表
                for (Map<String, Object> recordMap : recordList) {
                    ProductsDetailRes productsDetailRes = new ProductsDetailRes();

                    productsDetailRes.setSkuId(recordMap.get("skuId").toString());
                    if(recordMap.containsKey("proSkuTitle") && recordMap.get("proSkuTitle") != null) productsDetailRes.setProSkuTitle(recordMap.get("proSkuTitle").toString() );

                    if(recordMap.containsKey("proSkuSubTitle") && recordMap.get("proSkuSubTitle") != null ) productsDetailRes.setProSkuSubTitle(recordMap.get("proSkuSubTitle").toString() );

                    if(recordMap.containsKey("proSkuSkuPicJson") && recordMap.get("proSkuSkuPicJson") != null) productsDetailRes.setProSkuSkuPicJson(recordMap.get("proSkuSkuPicJson").toString());

                    if(recordMap.containsKey("skuSellPriceJson") && recordMap.get("skuSellPriceJson") != null ){
                        JSONArray array = JSONObject.parseArray(recordMap.get("skuSellPriceJson").toString());
                        productsDetailRes.setSkuSellPriceJson(recordMap.get("skuSellPriceJson").toString());
                    }

                    if (recordMap.containsKey("skuGmtCreateTime") && recordMap.get("skuGmtCreateTime") != null) {
                        LocalDateTime t = LocalDateTime.parse(recordMap.get("skuGmtCreateTime").toString(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        productsDetailRes.setSkuGmtCreateTime(t);
                    }

                    if(recordMap.containsKey("storeId") && recordMap.get("storeId") != null) productsDetailRes.setShopId(recordMap.get("storeId").toString());

                    if(recordMap.containsKey("storeName") && recordMap.get("storeName") != null) productsDetailRes.setStoreInfoName(recordMap.get("storeName").toString() );

                    if(recordMap.containsKey("businessCategory") && recordMap.get("businessCategory") != null ) productsDetailRes.setBusinessCategory(recordMap.get("businessCategory").toString() );

                    if(recordMap.containsKey("mainProducts") && recordMap.get("mainProducts") != null) productsDetailRes.setMainProducts(recordMap.get("mainProducts").toString());

                    if(recordMap.containsKey("businessArea") && recordMap.get("businessArea") != null) productsDetailRes.setBusinessArea(recordMap.get("businessArea").toString());

                    if(recordMap.containsKey("boothBusinessBoothCode") && recordMap.get("boothBusinessBoothCode") != null)productsDetailRes.setBoothBusinessBoothCode(recordMap.get("boothBusinessBoothCode").toString());

                    if(recordMap.containsKey("customerProfilesLevel") && recordMap.get("customerProfilesLevel") != null) productsDetailRes.setCustomerProfilesLevel(recordMap.get("customerProfilesLevel").toString());

                    if(recordMap.containsKey("approveState") && recordMap.get("approveState") != null) productsDetailRes.setApproveState(recordMap.get("approveState").toString());

                    if(recordMap.containsKey("enterpriseType") && recordMap.get("enterpriseType") != null ) productsDetailRes.setEnterpriseType(recordMap.get("enterpriseType").toString());

                    if(recordMap.containsKey("storeInfoStoreQrCode") && recordMap.get("storeInfoStoreQrCode") != null) productsDetailRes.setStoreInfoStoreQrCode(recordMap.get("storeInfoStoreQrCode").toString());

                    if(recordMap.containsKey("gmtCreateTime") && recordMap.get("gmtCreateTime") !=null) {
                        LocalDateTime t = LocalDateTime.parse(recordMap.get("gmtCreateTime").toString(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        productsDetailRes.setGmtCreateTime(t);
                    }

                    if(recordMap.containsKey("regionalId") && recordMap.get("regionalId") !=null) productsDetailRes.setRegionalIdJson(recordMap.get("regionalId").toString());

                    if(recordMap.containsKey("skuRegionalName") && recordMap.get("skuRegionalName") !=null) productsDetailRes.setSkuRegionalNameJson(recordMap.get("skuRegionalName").toString());

                    if(recordMap.containsKey("attributeMap") && recordMap.get("attributeMap") !=null) productsDetailRes.setAttributeMapJson(recordMap.get("attributeMap").toString());

                    productDetailResList.add(productsDetailRes);

                    categorys.put(recordMap.containsKey("fThreeCategoryCode") ? recordMap.get("fThreeCategoryCode").toString() : "", recordMap.containsKey("fThreeCategoryName") ? recordMap.get("fThreeCategoryName").toString() : "");
                    brands.put(recordMap.containsKey("proSkuBrandId") ? recordMap.get("proSkuBrandId").toString() : "", recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");
                    if (attributes.containsKey(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "")) {
                        Map<String, String> mapt = attributes.get(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "");
                        mapt.put(recordMap.containsKey("attributeValueId") ? recordMap.get("attributeValueId").toString() : "", recordMap.containsKey("value_Name") ? recordMap.get("value_Name").toString() : "");
                    } else {
                        Map<String, String> mapt = new HashMap<>();
                        mapt.put(recordMap.containsKey("attributeValueId") ? recordMap.get("attributeValueId").toString() : "", recordMap.containsKey("value_Name") ? recordMap.get("value_Name").toString() : "");
                        attributes.put(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "", mapt);
                    }
                    productsDetailRes.setSkuAuxiliaryUnit(recordMap.containsKey("skuAuxiliaryUnit") ? recordMap.get("skuAuxiliaryUnit").toString() : "");
                    productsDetailRes.setSkuName(recordMap.containsKey("proSkuSkuName")?recordMap.get("proSkuSkuName").toString() : "");
                }
                productsRes.setCategorys(categorys);
                productsRes.setBrands(brands);
                productsRes.setAttributes(attributes);
                productsRes.setProductDetailRes(productDetailResList);
                productsRes.setTotal(esPageRes.getRecordCount());
            }
        }
        res.setData(productsRes);
        res.setSuccess(true);
        res.setCode(200);
        res.setMessage("success.");
        return res;
    }

    @Override
    public ResponseBO<List<ProductDetailsRes>>  getProductsDetail(@Valid ProductDetailsReq productDetailsReq) throws IOException {
        ResponseBO<List<ProductDetailsRes>> res = new ResponseBO<List<ProductDetailsRes>>();
        List<ProductDetailsRes> reslist = new ArrayList<>();
        List<Map<String, Object>> list = productESServiceImpl.boolQueryForProductDetail(productDetailsReq);
        if (CollectionUtil.isNotEmpty(list)) {
            for(Map<String, Object> map : list){
                ProductDetailsRes productDetailsRes = new ProductDetailsRes();
                if(map.containsKey("skuId") && map.get("skuId") != null ) productDetailsRes.setSkuId(map.get("skuId").toString());

                if(map.containsKey("proSkuSkuName") && map.get("proSkuSkuName") != null ) productDetailsRes.setSkuName(map.get("proSkuSkuName").toString());

                if(map.containsKey("proSkuTitle") && map.get("proSkuTitle") != null )productDetailsRes.setSkuTitle(map.get("proSkuTitle").toString());

                if(map.containsKey("proSkuSubTitle") && map.get("proSkuSubTitle") != null )productDetailsRes.setSkuSubTitle(map.get("proSkuSubTitle").toString());

                if(map.containsKey("attributeMap") && map.get("attributeMap") !=null) productDetailsRes.setAttributeMapJson(map.get("attributeMap").toString());

                if(map.containsKey("skuSellPriceJson") && map.get("skuSellPriceJson") != null){
                    JSONArray array = JSONObject.parseArray(map.get("skuSellPriceJson").toString());
                    productDetailsRes.setPriceJson(JSONObject.toJSONString(array));
                }

                if(map.containsKey("skuIntroduce") && map.get("skuIntroduce") != null) productDetailsRes.setSkuIntroduce(map.get("skuIntroduce").toString());

                if(map.containsKey("proSkuSkuPicJson") && map.get("proSkuSkuPicJson") != null ) productDetailsRes.setProSkuSkuPicJson(map.get("proSkuSkuPicJson").toString());

                if(map.containsKey("regionalCode") && map.get("regionalCode") != null ) productDetailsRes.setRegionalId(map.get("regionalCode").toString());

                if(map.containsKey("regionalName") && map.get("regionalName") != null ) productDetailsRes.setRegionalName(map.get("regionalName").toString());
                reslist.add(productDetailsRes);
            }
        }
        res.setData(reslist);
        res.setSuccess(true);
        res.setCode(200);
        res.setMessage("success.");
        return res;
    }


    @Override
    public ResponseBO<CategoryRes> categoryRes(@Valid CategoryReq categoryReq) {
        ResponseBO<CategoryRes> res = new ResponseBO<CategoryRes>();
        List<Map<String, Object>> result = productESServiceImpl.findByIds(categoryReq);
        if (CollectionUtil.isNotEmpty(result)) {
            CategoryRes categoryRes = new CategoryRes();
            String skuid = "";
            Map<String, CategoryVO> maps = new HashMap<>();
            for(Map<String, Object> map : result){
                CategoryVO categoryVO = new CategoryVO();
                if(map.containsKey("skuId")){
                    if(map.containsKey("fOneCategoryId")) categoryVO.setFOneCategoryId(map.get("fOneCategoryId").toString());
                    if(map.containsKey("fOneCategoryCode")) categoryVO.setFOneCategoryCode(map.get("fOneCategoryCode").toString());
                    if(map.containsKey("fOneCategoryName")) categoryVO.setFOneCategoryName(map.get("fOneCategoryName").toString());
                    if(map.containsKey("fTwoCategoryId")) categoryVO.setFTwoCategoryId(map.get("fTwoCategoryId").toString());
                    if(map.containsKey("fTwoCategoryCode")) categoryVO.setFTwoCategoryCode(map.get("fTwoCategoryCode").toString());
                    if(map.containsKey("fTwoCategoryName")) categoryVO.setFTwoCategoryName(map.get("fTwoCategoryName").toString());
                    if(map.containsKey("fThreeCategoryId")) categoryVO.setFThreeCategoryId(map.get("fThreeCategoryId").toString());
                    if(map.containsKey("fThreeCategoryCode")) categoryVO.setFThreeCategoryCode(map.get("fThreeCategoryCode").toString());
                    if(map.containsKey("fThreeCategoryName")) categoryVO.setFThreeCategoryName(map.get("fThreeCategoryName").toString());
                    maps.put(map.get("skuId").toString(),categoryVO);
                }
            }
            categoryRes.setMaplist(maps);
            res.setData(categoryRes);
        }
        res.setMessage("success");
        res.setCode(200);
        res.setSuccess(true);
        return res;
    }
}
