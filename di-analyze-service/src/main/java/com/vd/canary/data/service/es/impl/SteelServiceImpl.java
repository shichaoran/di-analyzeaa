package com.vd.canary.data.service.es.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.ProductsReq;
import com.vd.canary.data.api.request.es.SpecCommand;
import com.vd.canary.data.api.request.es.SteelReq;
import com.vd.canary.data.api.response.es.ProductsRes;
import com.vd.canary.data.api.response.es.SteelRes;
import com.vd.canary.data.api.response.es.vo.ProductsDetailRes;
import com.vd.canary.data.api.response.es.vo.SteelVO;
import com.vd.canary.data.common.es.helper.ESPageRes;
import com.vd.canary.data.common.es.helper.ElasticsearchUtil;
import com.vd.canary.data.common.es.model.FinalSteel;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.data.service.es.SteelService;
import com.vd.canary.utils.DateUtil;
import com.vd.canary.utils.JSONUtil;
import com.vd.canary.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.TransformedMap;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.nustaq.kson.JSonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author WangRuilin
 * @Date 2020/4/19 14:53
 */
@Slf4j
@Service
public class SteelServiceImpl implements SteelService {

    @Autowired
    private ProductESServiceImpl productESServiceImpl;

    //    @Override
    public ResponseBO<SteelRes> getProductByCategory1(@Valid SteelReq steelReq) {
        ResponseBO<SteelRes> res = new ResponseBO<SteelRes>();
        SteelRes steelRes = new SteelRes();
        Map<String, String> categorys = new HashMap<String, String>();
        categorys.put("0", "装配式建筑");
        categorys.put("1", "金属矿");
        categorys.put("2", "铜矿");
        steelRes.setCategorys(categorys);
        Map<String, String> brands = new HashMap<String, String>();
        brands.put("0", "抚顺特钢");
        brands.put("1", "不锈钢");
        brands.put("2", "不锈钢铁");
        steelRes.setBrands(brands);
        Map<String, Map<String, String>> s = new HashMap<String, Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("1", "红色");
        map.put("2", "黄色");
        s.put("颜色", map);
        steelRes.setAttributes(s);
        List<SteelVO> list = new ArrayList<SteelVO>();
        SteelVO steelVO = new SteelVO();
        Map<String, Map<String, String>> s1 = new HashMap<>();
        Map<String, String> mapsub = new HashMap<String, String>();
        mapsub.put("1", "黑色");
        mapsub.put("2", "白色");
        s1.put("颜色", mapsub);
//        steelVO.setAttributeMap(s1);
        steelVO.setSkuSellPriceJson("[\"100\"]");
        steelVO.setSkuSellPriceType(0);
        steelVO.setSkuGmtCreateTime(null);
        steelVO.setSkuAuxiliaryUnit("顿");
//        steelVO.setFThreeCategoryName("建筑钢");
//        steelVO.setFTwoCategoryName("钢");
        steelVO.setFThreeCategoryCode("H101010");
        steelVO.setSkuGmtCreateTime(LocalDateTime.ofInstant(DateUtil.currentDate().toInstant(), ZoneId.systemDefault()));
        list.add(steelVO);
        steelRes.setSteelVORes(list);

        steelRes.setTotal(100);
        res.setData(steelRes);
        res.setCode(200);
        res.setSuccess(true);
        res.setMessage("success");
        return res;
    }


    @Override
    public ResponseBO<SteelRes> getProductByCategory(@Valid SteelReq steelReq) {
        ResponseBO<SteelRes> res = new ResponseBO<SteelRes>();
        SteelRes steelRes = new SteelRes();
        log.info("getProductByCategory,threeCategoryReq:" + JSONObject.toJSON(steelReq).toString());
        ESPageRes esPageRes = productESServiceImpl.boolQueryByKeyword(steelReq.getPageNum(), steelReq.getPageSize(), steelReq);                  //                                       new SteelReq());
        if (esPageRes != null) {
            List<Map<String, Object>> recordList = esPageRes.getRecordList();
            if (recordList != null && recordList.size() > 0) {
                Map<String, String> categorys = new HashMap<>();//fThreeCategoryId:fThreeCategoryName
                Map<String, String> brands = new HashMap<>(); //proSkuBrandId:bBrandName
                Map<String, Map<String, String>> attributes = new HashMap<>(); //属性
                List<SteelVO> steelVOS = new ArrayList<>(); //商品详细列表
                Map<String, String> spuNames = new HashMap<>();//spu名称
                List<SpecCommand> specCommands = new ArrayList<>();
                SpecCommand specCommand = new SpecCommand();
                for (Map<String, Object> recordMap : recordList) {
//                    ProductsDetailRes productsDetailRes = new ProductsDetailRes();

                    SteelVO steelVO = new SteelVO();
                    Map<String, Map<String, String>> skuAttributes = new HashMap<>();

                    steelVO.setSkuID(recordMap.containsKey("skuId") ? recordMap.get("skuId").toString() : "");
                    steelVO.setSkuName(recordMap.containsKey("proSkuSkuName") ? recordMap.get("proSkuSkuName").toString() : "");
                    steelVO.setSpuID(recordMap.containsKey("proSkuSpuId") ? recordMap.get("proSkuSpuId").toString() : "");
                    steelVO.setSpuName(recordMap.containsKey("proSkuSpuName") ? recordMap.get("proSkuSpuName").toString() : "");
                    steelVO.setSpuName(recordMap.containsKey("skuRegionalName") ? recordMap.get("skuRegionalName").toString() : "");
                    steelVO.setShopId(recordMap.containsKey("storeId") ? recordMap.get("storeId").toString() : "");

                    if (recordMap.containsKey("attributeMap")) {
//                        steelVO.setAttributeMapJson(recordMap.get("attributeMap").toString());
                        JSONArray array = JSONObject.parseArray(recordMap.get("attributeMap").toString());

                        if (array != null && array.size() > 0) {
                            Map map = new HashMap();
                            for (int i = 0; i < array.size(); i++) {
                                if (array.getJSONObject(i).containsKey("attributeName") && array.getJSONObject(i).get("attributeType").equals("0") && array.getJSONObject(i).get("attributeName") != null) {
                                    String attributeName = array.getJSONObject(i).get("attributeName").toString();
                                    if (array.getJSONObject(i).containsKey("attributeValue")) {
                                        JSONArray arr = JSONObject.parseArray(array.getJSONObject(i).get("attributeValue").toString());
                                        if (arr != null && arr.size() > 0) {
                                            for (int j = 0; j < arr.size(); j++) {
                                                if (arr.getJSONObject(j).containsKey("attributeValueId")) {
                                                    String attributeValueId = arr.getJSONObject(j).get("attributeValueId").toString();
                                                    if (arr.getJSONObject(j).containsKey("attributeValueName")) {
                                                        String attributeValueName = arr.getJSONObject(j).get("attributeValueName").toString();

                                                        if (attributeName.equals("规格")) {
                                                            if (Double.parseDouble(attributeValueName) >= specCommand.getLongMin() && Double.parseDouble(attributeValueName) <= specCommand.getLongMax()) {
                                                                map.put(attributeValueId, attributeValueName);
                                                            }
                                                            if (Double.parseDouble(attributeValueName) >= specCommand.getThickMin() && Double.parseDouble(attributeValueName) <= specCommand.getThickMax()) {
                                                                map.put(attributeValueId, attributeValueName);
                                                            }
                                                            if (Double.parseDouble(attributeValueName) >= specCommand.getWideMin() && Double.parseDouble(attributeValueName) <= specCommand.getWideMax()) {
                                                                map.put(attributeValueId, attributeValueName);
                                                            }

                                                        }
                                                        map.put(attributeValueId, attributeValueName);
                                                        skuAttributes.put(attributeName, map);
                                                        steelVO.setSkuAttributes(skuAttributes);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    attributes.put(attributeName, map);
                                }
                            }
                        }
                    }

                    if (recordMap.containsKey("skuSellPriceJson")) {
                        JSONArray array = JSONObject.parseArray(recordMap.get("skuSellPriceJson").toString());
                        steelVO.setSkuSellPriceJson(JSONObject.toJSONString(array));
                    }
                    steelVO.setSkuSellPriceType(recordMap.containsKey("skuSellPriceType") ? Integer.parseInt(recordMap.get("skuSellPriceType").toString()) : 0);
                    if (recordMap.containsKey("skuGmtCreateTime")) {
                        LocalDateTime t = LocalDateTime.parse(recordMap.get("skuGmtCreateTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        steelVO.setSkuGmtCreateTime(t);
                    }
                    steelVO.setSkuAuxiliaryUnit(recordMap.containsKey("skuAuxiliaryUnit") ? recordMap.get("skuAuxiliaryUnit").toString() : "");

                    steelVO.setFThreeCategoryCode(recordMap.containsKey("fThreeCategoryCode") ? recordMap.get("fThreeCategoryCode").toString() : "");
                    steelVO.setFThreeCategoryId(recordMap.containsKey("fThreeCategoryId") ? recordMap.get("fThreeCategoryId").toString() : "");
                    steelVO.setFThreeCategoryName(recordMap.containsKey("fThreeCategoryName") ? recordMap.get("fThreeCategoryName").toString() : "");
                    steelVO.setBrand(recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");


                    Object fThreeCategoryCode = recordMap.get("fThreeCategoryCode");
                    if (ObjectUtil.isNotEmpty(fThreeCategoryCode)) {
                        log.info("fThreeCategoryCode" + fThreeCategoryCode.toString());
                        if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fThreeCategoryCode))) {
                            List<String> fthreecategorylist = JSONArray.parseArray(JSONUtil.toJSONString(fThreeCategoryCode), String.class);
                            for (int i = 0; i < fthreecategorylist.size(); i++) {
                                steelVO.setFThreeCategoryCode(fthreecategorylist.get(i));
                                FinalSteel finalSteel = new FinalSteel();
                                for (Integer j = 0; j < finalSteel.getList().size(); j++) {
                                    if (fthreecategorylist.get(i).equals(finalSteel.getList().get(j))) {
                                        Object fThreeCategoryName = recordMap.get("fThreeCategoryName");
                                        if (ObjectUtil.isNotEmpty(fThreeCategoryName)) {
                                            log.info("fThreeCategoryName" + fThreeCategoryName.toString());
                                            if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fThreeCategoryName))) {
                                                List<String> fthreecategorynamelist = JSONArray.parseArray(JSONUtil.toJSONString(fThreeCategoryName), String.class);
                                                categorys.put(fthreecategorylist.get(i), fthreecategorynamelist.get(j));
                                            }
                                        }

                                        Object fOneCategoryCode = recordMap.get("fOneCategoryCode");
                                        Object fOneCategoryName = recordMap.get("fOneCategoryName");
                                        if (ObjectUtil.isNotEmpty(fOneCategoryCode)) {
                                            log.info("fOneCategoryCode" + fOneCategoryCode.toString());
                                            if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fOneCategoryCode))) {
                                                List<String> fonecategorylist = JSONArray.parseArray(JSONUtil.toJSONString(fOneCategoryCode), String.class);
                                                if (ObjectUtil.isNotEmpty(fOneCategoryName)) {
                                                    log.info("fOneCategoryName" + fOneCategoryCode.toString());
                                                    if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fOneCategoryName))) {
                                                        List<String> fonecategorynamelist = JSONArray.parseArray(JSONUtil.toJSONString(fOneCategoryName), String.class);
                                                        for (int k = 0; k < fonecategorylist.size(); k++) {
                                                            categorys.put(fonecategorylist.get(k), fonecategorynamelist.get(k));
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Object fTwoCategoryCode = recordMap.get("fTwoCategoryCode");
                                        Object fTwoCategoryName = recordMap.get("fTwoCategoryName");
                                        if (ObjectUtil.isNotEmpty(fTwoCategoryCode)) {
                                            log.info("fTwoCategoryCode" + fTwoCategoryCode.toString());
                                            if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fTwoCategoryCode))) {
                                                List<String> ftwocategorylist = JSONArray.parseArray(JSONUtil.toJSONString(fTwoCategoryCode), String.class);
                                                if (ObjectUtil.isNotEmpty(fTwoCategoryName)) {
                                                    log.info("fTwoCategoryName" + fTwoCategoryName.toString());
                                                    if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fTwoCategoryName))) {
                                                        List<String> ftwocategorynamelist = JSONArray.parseArray(JSONUtil.toJSONString(fTwoCategoryName), String.class);
                                                        for (int k = 0; k < ftwocategorylist.size(); k++) {
                                                            categorys.put(ftwocategorylist.get(k), ftwocategorynamelist.get(k));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    steelVOS.add(steelVO);
                    brands.put(recordMap.containsKey("proSkuBrandId") ? recordMap.get("proSkuBrandId").toString() : "", recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");
                    spuNames.put(recordMap.containsKey("proSkuSpuId") ? recordMap.get("proSkuSpuId").toString() : "", recordMap.containsKey("proSkuSpuName") ? recordMap.get("proSkuSpuName").toString() : "");
                }
                steelRes.setCategorys(categorys);
                steelRes.setBrands(brands);
                steelRes.setAttributes(attributes);
                steelRes.setSteelVORes(steelVOS);
                steelRes.setTotal(esPageRes.getRecordCount());
                steelRes.setSpuNames(spuNames);
                steelRes.setCurrentPage(esPageRes.getCurrentPage());
            }
        }
        res.setData(steelRes);
        res.setSuccess(true);
        res.setCode(200);
        res.setMessage("success.");
        return res;
    }

}


//
//                    Object fOneCategoryCode = recordMap.get("fOneCategoryCode");
//                    Object fOneCategoryName = recordMap.get("fOneCategoryName");
//                    if (ObjectUtil.isNotEmpty(fOneCategoryCode)) {
//                        log.info("fOneCategoryCode" + fOneCategoryCode.toString());
//                        if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fOneCategoryCode))) {
//                            List<String> fonecategorylist = JSONArray.parseArray(JSONUtil.toJSONString(fOneCategoryCode), String.class);
//                            if (ObjectUtil.isNotEmpty(fOneCategoryName)) {
//                                log.info("fOneCategoryName" + fOneCategoryCode.toString());
//                                if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fOneCategoryName))) {
//                                    List<String> fonecategorynamelist = JSONArray.parseArray(JSONUtil.toJSONString(fOneCategoryName), String.class);
//                                    for (int i = 0; i < fonecategorylist.size(); i++) {
//                                        for (int j = 0; j < fonecategorynamelist.size(); j++) {
//                                            if (i == j) {
//                                                categorys.put(fonecategorylist.get(i), fonecategorynamelist.get(j));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    Object fTwoCategoryCode = recordMap.get("fTwoCategoryCode");
//                    Object fTwoCategoryName = recordMap.get("fTwoCategoryName");
//                    if (ObjectUtil.isNotEmpty(fTwoCategoryCode)) {
//                        log.info("fTwoCategoryCode" + fTwoCategoryCode.toString());
//                        if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fTwoCategoryCode))) {
//                            List<String> ftwocategorylist = JSONArray.parseArray(JSONUtil.toJSONString(fTwoCategoryCode), String.class);
//                            if (ObjectUtil.isNotEmpty(fTwoCategoryName)) {
//                                log.info("fTwoCategoryName" + fTwoCategoryName.toString());
//                                if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fTwoCategoryName))) {
//                                    List<String> ftwocategorynamelist = JSONArray.parseArray(JSONUtil.toJSONString(fTwoCategoryName), String.class);
//                                    for (int i = 0; i < ftwocategorylist.size(); i++) {
//                                        for (int j = 0; j < ftwocategorynamelist.size(); j++) {
//                                            if (i == j) {
//                                                categorys.put(ftwocategorylist.get(i),ftwocategorynamelist.get(j));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//
//                    Object fThreeCategoryCode = recordMap.get("fThreeCategoryCode");
//                    Object fThreeCategoryName = recordMap.get("fThreeCategoryName");
//                    if (ObjectUtil.isNotEmpty(fThreeCategoryCode)) {
//                        log.info("fThreeCategoryCode" + fThreeCategoryCode.toString());
//                        if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fThreeCategoryCode))) {
//                            List<String> fthreecategorylist = JSONArray.parseArray(JSONUtil.toJSONString(fThreeCategoryCode), String.class);
//                            if (ObjectUtil.isNotEmpty(fThreeCategoryName)) {
//                                log.info("fThreeCategoryName" + fThreeCategoryName.toString());
//                                if (com.vd.canary.utils.StringUtil.isNotEmpty(JSONUtil.toJSONString(fThreeCategoryName))) {
//                                    List<String> fthreecategorynamelist = JSONArray.parseArray(JSONUtil.toJSONString(fThreeCategoryName), String.class);
//                                    for (int i = 0; i < fthreecategorylist.size(); i++) {
//                                        for (int j = 0; j < fthreecategorynamelist.size(); j++) {
//                                            if (i == j) {
//                                                categorys.put(fthreecategorylist.get(i),fthreecategorynamelist.get(j));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }


//            brands.put(recordMap.containsKey("proSkuBrandId") ? recordMap.get("proSkuBrandId").toString() : "", recordMap.containsKey("bBrandName") ? recordMap.get("bBrandName").toString() : "");
//            spuNames.put(recordMap.containsKey("proSkuSpuId") ? recordMap.get("proSkuSpuId").toString() : "", recordMap.containsKey("proSkuSpuName") ? recordMap.get("proSkuSpuName").toString() : "");


//                    if (attributes.containsKey(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "")) {
//                        Map<String, String> mapt = attributes.get(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "");
//                        mapt.put(recordMap.containsKey("attributeValueId") ? recordMap.get("attributeValueId").toString() : "", recordMap.containsKey("value_Name") ? recordMap.get("value_Name").toString() : "");
//                    } else {
//                        Map<String, String> mapt = new HashMap<>();
//                        mapt.put(recordMap.containsKey("attributeValueId") ? recordMap.get("attributeValueId").toString() : "", recordMap.containsKey("value_Name") ? recordMap.get("value_Name").toString() : "");
//                        attributes.put(recordMap.containsKey("attributeName") ? recordMap.get("attributeName").toString() : "", mapt);
//                    }

//        }
//        steelRes.setCategorys(categorys);
//        steelRes.setBrands(brands);
//        steelRes.setAttributes(attributes);
//        steelRes.setSteelVORes(steelVOS);
//        steelRes.setTotal(esPageRes.getRecordCount());
//        steelRes.setSpuNames(spuNames);
//        steelRes.setCurrentPage(esPageRes.getCurrentPage());
//    }
//}
//        res.setData(steelRes);
//                res.setSuccess(true);
//                res.setCode(200);
//                res.setMessage("success.");
//                return res;
//                }
//                }
