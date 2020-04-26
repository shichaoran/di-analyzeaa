package com.vd.canary.data.service.es.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.CategoryReq;
import com.vd.canary.data.api.request.es.ProductDetailsReq;
import com.vd.canary.data.api.request.es.ProductsReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.CategoryRes;
import com.vd.canary.data.api.response.es.ProductSkuInfoVO;
import com.vd.canary.data.api.response.es.ProductSpuInfoResponse;
import com.vd.canary.data.api.response.es.ProductsExistsShopRes;
import com.vd.canary.data.api.response.es.vo.CategoryVO;
import com.vd.canary.data.common.es.helper.ESPageRes;
import com.vd.canary.data.api.response.es.ProductsRes;
import com.vd.canary.data.api.response.es.vo.ProductsDetailRes;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.service.es.ProductsService;
import com.vd.canary.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
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
        ESPageRes esPageRes = null;
        boolean existsShopFlag = false;
        // 搜索是否能精准定位到店铺
        List<Map<String, Object>> existsShop = productESServiceImpl.boolQueryByExistsShopKeyword(productsReq);
        if(existsShop != null && existsShop.size() == 1){
            Map<String, Object> existsShopMap = existsShop.get(0);
            ProductsExistsShopRes productsExistsShopRes = new ProductsExistsShopRes();
            if( existsShopMap.containsKey("logoImageUrl") && existsShopMap.get("logoImageUrl") != null ) productsExistsShopRes.setLogoImageUrl(existsShopMap.get("logoImageUrl").toString());

            if( existsShopMap.containsKey("storeId") && existsShopMap.get("storeId") != null ) productsExistsShopRes.setStoreId(existsShopMap.get("storeId").toString());

            if( existsShopMap.containsKey("storeName") && existsShopMap.get("storeName") != null ) productsExistsShopRes.setStoreName(existsShopMap.get("storeName").toString());

            if( existsShopMap.containsKey("boothBusinessBoothCode") && existsShopMap.get("boothBusinessBoothCode") != null ) productsExistsShopRes.setBoothCode(existsShopMap.get("boothBusinessBoothCode").toString());

            if( existsShopMap.containsKey("customerProfilesLevel") && existsShopMap.get("customerProfilesLevel") != null ) productsExistsShopRes.setMemberOrder(existsShopMap.get("customerProfilesLevel").toString());

            productsRes.setProductsExistsShopRes(productsExistsShopRes);

            if( existsShopMap.containsKey("storeId") && existsShopMap.get("storeId") != null ) esPageRes =
                    productESServiceImpl.allProductByStoreId(productsReq.getPageNum(), productsReq.getPageSize(),existsShopMap.get("storeId").toString());
            existsShopFlag = true;
        }

        if (!existsShopFlag){
            // 商品关键字搜索
            esPageRes = productESServiceImpl.boolQueryByKeyword(productsReq.getPageNum(), productsReq.getPageSize(), productsReq);
        }

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

                    if(recordMap.containsKey("proSkuSpuId") && recordMap.get("proSkuSpuId") !=null) productsDetailRes.setProSkuSpuId(recordMap.get("proSkuSpuId").toString());

                    productDetailResList.add(productsDetailRes);

                    categorys.put(recordMap.containsKey("fOneCategoryCode") ? recordMap.get("fOneCategoryCode").toString() : "", recordMap.containsKey("fOneCategoryName") ? recordMap.get("fOneCategoryName").toString() : "");
                    categorys.put(recordMap.containsKey("fThreeCategoryCode") ? recordMap.get("fThreeCategoryCode").toString() : "", recordMap.containsKey("fThreeCategoryName") ? recordMap.get("fThreeCategoryName").toString() : "");
                    categorys.put(recordMap.containsKey("fTwoCategoryCode") ? recordMap.get("fTwoCategoryCode").toString() : "", recordMap.containsKey("fTwoCategoryName") ? recordMap.get("fTwoCategoryName").toString() : "");
                    brands.put(recordMap.containsKey("proSkuBrandId") ? recordMap.get("proSkuBrandId").toString() : "", recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");

                    if(recordMap.containsKey("attributeMap")){
                        productsDetailRes.setAttributeMapJson(recordMap.get("attributeMap").toString());
                        JSONArray array = JSONObject.parseArray(recordMap.get("attributeMap").toString());

                        if (array != null && array.size() > 0) {
                            for (int i=0;i<array.size();i++){
                                if (array.getJSONObject(i).containsKey("attributeName") && array.getJSONObject(i).get("attributeType").equals("0") && array.getJSONObject(i).get("attributeName") != null){
                                    String attributeName =  array.getJSONObject(i).get("attributeName").toString();
                                    if (array.getJSONObject(i).containsKey("attributeValue")) {
                                        JSONArray arr = JSONObject.parseArray(array.getJSONObject(i).get("attributeValue").toString());
                                        if (arr != null && arr.size() > 0) {
                                            for (int j = 0; j < arr.size(); j++) {
                                                Map map = new HashMap();
                                                if (arr.getJSONObject(j).containsKey("attributeValueId")) {
                                                    String attributeValueId = arr.getJSONObject(j).get("attributeValueId").toString();
                                                    if (arr.getJSONObject(j).containsKey("attributeValueName") ) {
                                                        String attributeValueName = arr.getJSONObject(j).get("attributeValueName").toString();
                                                        map.put(attributeValueId, attributeValueName);
                                                        attributes.put(attributeName, map);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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

                    if(recordMap.containsKey("proSkuSpuId") && recordMap.get("proSkuSpuId") !=null) productsDetailRes.setProSkuSpuId(recordMap.get("proSkuSpuId").toString());

                    productDetailResList.add(productsDetailRes);

                    categorys.put(recordMap.containsKey("fOneCategoryCode") ? recordMap.get("fOneCategoryCode").toString() : "", recordMap.containsKey("fOneCategoryName") ? recordMap.get("fOneCategoryName").toString() : "");
                    categorys.put(recordMap.containsKey("fThreeCategoryCode") ? recordMap.get("fThreeCategoryCode").toString() : "", recordMap.containsKey("fThreeCategoryName") ? recordMap.get("fThreeCategoryName").toString() : "");
                    categorys.put(recordMap.containsKey("fTwoCategoryCode") ? recordMap.get("fTwoCategoryCode").toString() : "", recordMap.containsKey("fTwoCategoryName") ? recordMap.get("fTwoCategoryName").toString() : "");
                    brands.put(recordMap.containsKey("proSkuBrandId") ? recordMap.get("proSkuBrandId").toString() : "", recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");



                    if(recordMap.containsKey("attributeMap")){
                        productsDetailRes.setAttributeMapJson(recordMap.get("attributeMap").toString());
                        JSONArray array = JSONObject.parseArray(recordMap.get("attributeMap").toString());

                        if (array != null && array.size() > 0) {
                            for (int i=0;i<array.size();i++){
                                if (array.getJSONObject(i).containsKey("attributeName") && array.getJSONObject(i).get("attributeType").equals("0") && array.getJSONObject(i).get("attributeName") != null){
                                    String attributeName =  array.getJSONObject(i).get("attributeName").toString();
                                    if (array.getJSONObject(i).containsKey("attributeValue")) {
                                        JSONArray arr = JSONObject.parseArray(array.getJSONObject(i).get("attributeValue").toString());
                                        if (arr != null && arr.size() > 0) {
                                            for (int j = 0; j < arr.size(); j++) {
                                                Map map = new HashMap();
                                                if (arr.getJSONObject(j).containsKey("attributeValueId")) {
                                                    String attributeValueId = arr.getJSONObject(j).get("attributeValueId").toString();
                                                    if (arr.getJSONObject(j).containsKey("attributeValueName") ) {
                                                        String attributeValueName = arr.getJSONObject(j).get("attributeValueName").toString();
                                                        map.put(attributeValueId, attributeValueName);
                                                        attributes.put(attributeName, map);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
    public ResponseBO<ProductSpuInfoResponse>  getProductsDetail(@Valid ProductDetailsReq productDetailsReq) throws IOException {
        ResponseBO<ProductSpuInfoResponse> res = new ResponseBO<ProductSpuInfoResponse>();
        ProductSpuInfoResponse productSpuInfoResponse = new ProductSpuInfoResponse();
        //Map<String, Object> map = productESServiceImpl.findById(productDetailsReq.getSkuId());
        List<Map<String, Object>> mapTemp = productESServiceImpl.boolQueryForProductDetail(productDetailsReq);
        if(mapTemp == null && mapTemp.size() == 0){
            return res;
        }
        Map<String, Object> map = mapTemp.get(0);
        if(map != null){
            if(map.containsKey("proSkuSpuId") && map.get("proSkuSpuId") != null ) productSpuInfoResponse.setSpuId(map.get("proSkuSpuId").toString());

            if(map.containsKey("proSkuSpuName") && map.get("proSkuSpuName") != null ) productSpuInfoResponse.setSpuName(map.get("proSkuSpuName").toString());

            if(map.containsKey("proSpuSpuPic") && map.get("proSpuSpuPic") != null ) productSpuInfoResponse.setSpuPic(map.get("proSpuSpuPic").toString());

            if(map.containsKey("spuTitle") && map.get("spuTitle") != null ) productSpuInfoResponse.setSpuTitle(map.get("spuTitle").toString());

            //if(map.containsKey("attributeCode") && map.get("attributeCode") != null ) productSpuInfoResponse.setSpuAttributeMapJson
            // (map.get( "attributeCode").toString());
            if(map.containsKey("spuAttributeMap") && map.get("spuAttributeMap") != null ) productSpuInfoResponse.setSpuAttributeMapJson(map.get( "spuAttributeMap").toString());

            ProductDetailsReq req = new ProductDetailsReq();
            req.setSpuId(productDetailsReq.getSpuId());
            req.setStoreId(productDetailsReq.getStoreId());
            List<Map<String, Object>> list = productESServiceImpl.boolQueryForProductDetail(req);
            List<ProductSkuInfoVO> productSkuInfoVOS = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(list)) {
                for(Map<String, Object> submap : list){
                    ProductSkuInfoVO productSkuInfoVO = new ProductSkuInfoVO();
                    if(submap.containsKey("skuId") && submap.get("skuId") != null ) productSkuInfoVO.setSkuId(submap.get("skuId").toString());

                    if(submap.containsKey("proSkuSkuName") && submap.get("proSkuSkuName") != null ) productSkuInfoVO.setSkuName(submap.get("proSkuSkuName").toString());

                    if(submap.containsKey("proSkuTitle") && submap.get("proSkuTitle") != null ) productSkuInfoVO.setSkuTitle(submap.get("proSkuTitle").toString());

                    if(submap.containsKey("proSkuSubTitle") && submap.get("proSkuSubTitle") != null ) productSkuInfoVO.setSkuSubTitle(submap.get("proSkuSubTitle").toString());

                    if(submap.containsKey("attributeMap") && submap.get("attributeMap") !=null) productSkuInfoVO.setAttributeMapJson(submap.get("attributeMap").toString());

                    if(submap.containsKey("skuSellPriceJson") && submap.get("skuSellPriceJson") != null){
                        JSONArray array = JSONObject.parseArray(submap.get("skuSellPriceJson").toString());
                        productSkuInfoVO.setPriceJson(JSONObject.toJSONString(array));
                    }

                    if(submap.containsKey("skuIntroduce") && submap.get("skuIntroduce") != null) productSkuInfoVO.setSkuIntroduce(submap.get("skuIntroduce").toString());

                    if(submap.containsKey("proSkuSkuPicJson") && submap.get("proSkuSkuPicJson") != null ) productSkuInfoVO.setProSkuSkuPicJson(submap.get("proSkuSkuPicJson").toString());

                    if(submap.containsKey("regionalId") && submap.get("regionalId") !=null){
                        JSONArray array = JSONArray.parseArray(submap.get("regionalId").toString());
                        if( array != null){
                            productSkuInfoVO.setRegionalId(JSONObject.parseArray(array.toJSONString(), String.class));
                        }
                    }

                    if(submap.containsKey("skuRegionalName") && submap.get("skuRegionalName") !=null) {
                        JSONArray array = JSONArray.parseArray(submap.get("skuRegionalName").toString());
                        if(array != null){
                            productSkuInfoVO.setRegionalName(JSONObject.parseArray(array.toJSONString(), String.class));
                        }
                    }

                    if(submap.containsKey("warehouseId") && submap.get("warehouseId") != null ) productSkuInfoVO.setWarehouseId(submap.get("warehouseId").toString());

                    if(submap.containsKey("warehouseName") && submap.get("warehouseName") != null ) productSkuInfoVO.setWarehouseName(submap.get("warehouseName").toString());

                    if(submap.containsKey("inventory") && submap.get("inventory") != null ) productSkuInfoVO.setInventory(submap.get("inventory").toString());

                    productSkuInfoVOS.add(productSkuInfoVO);
                }
                productSpuInfoResponse.setProductSkuInfoVO(productSkuInfoVOS);
            }
        }
        res.setData(productSpuInfoResponse);
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
                    //if(map.containsKey("fOneCategoryId")) categoryVO.setFOneCategoryId(map.get("fOneCategoryId").toString());
                    if(map.containsKey("fOneCategoryCode")) categoryVO.setFOneCategoryCode(map.get("fOneCategoryCode").toString());
                    if(map.containsKey("fOneCategoryName")) categoryVO.setFOneCategoryName(map.get("fOneCategoryName").toString());
                    //if(map.containsKey("fTwoCategoryId")) categoryVO.setFTwoCategoryId(map.get("fTwoCategoryId").toString());
                    if(map.containsKey("fTwoCategoryCode")) categoryVO.setFTwoCategoryCode(map.get("fTwoCategoryCode").toString());
                    if(map.containsKey("fTwoCategoryName")) categoryVO.setFTwoCategoryName(map.get("fTwoCategoryName").toString());
                    //if(map.containsKey("fThreeCategoryId")) categoryVO.setFThreeCategoryId(map.get("fThreeCategoryId").toString());
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
