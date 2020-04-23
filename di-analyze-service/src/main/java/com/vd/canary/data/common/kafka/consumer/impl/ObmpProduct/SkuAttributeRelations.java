package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.core.bo.ResponsePageBO;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.obmp.product.api.feign.BigDataApiFeign;
import com.vd.canary.obmp.product.api.request.spu.SpuAttributeRelationsReq;
import com.vd.canary.obmp.product.api.response.attribute.AttributeManagementDetailResp;
import com.vd.canary.obmp.product.api.response.attribute.AttributeValueResp;
import com.vd.canary.obmp.product.api.response.sku.SkuAttributeRelationsResp;
import com.vd.canary.obmp.product.api.response.spu.vo.SpuAttributeVO;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class SkuAttributeRelations implements Function {

    @Autowired
    private ProductESServiceImpl productESServiceImplTemp;

    @Autowired
    private BigDataApiFeign bigDataApiFeign;

    @Override
    public void performES(String msg) {
        log.info("SkuAttributeRelations.msg" + msg);
        if(StringUtils.isEmpty(msg)){
            return;
        }
        HashMap hashMap = JSON.parseObject(msg, HashMap.class);
        String type = (String) hashMap.get("type");
        String skuid = null;
        HashMap<String,Object> binlogMap = null;
        if(hashMap.containsKey("info")){
            binlogMap = JSON.parseObject(hashMap.get("info").toString(), HashMap.class);
        }
        ProductsTO productsTO = null;
        if (type.equals("insert") || type.equals("update")) {
            if(binlogMap != null && binlogMap.size() > 0){
                skuid = binlogMap.get("sku_id").toString();
                try {
                    Map<String, Object> esMap = productESServiceImplTemp.findById(skuid);
                    log.info("SkuAttributeRelations.performES,brand_id.esMap={}.", JSONUtil.toJSON(esMap).toJSONString());
                    if(esMap != null){
                        Map<String, Object> resjson = reSetSkuValue(esMap, binlogMap);
                        resjson = reSetSpuValue(resjson, binlogMap);
                        productESServiceImplTemp.updateProduct(resjson);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(type.equals("update")){
            //TODO
        }
    }

    /**
     * 因为商品和属性是一对多的关系，索引里这些字段目前不用：attributeCode，attributeName，value_Name，attributeId，attributeValueId，attributeType
     *  attributeMap 字段格式如下：
     * [{
     *      "attributeType": "1",
     *      "attributeId": "1252129898609266689",
     *      "attributeName": "颜色",
     *      "attributeValue": [{"attributeValueId":"1252129898630238210","attributeValueName":"红"},{"attributeValueId":"1252129898651209730","attributeValueName":"蓝"}]
     * }]
     */
    public Map<String, Object> reSetSkuValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("attribute_id") && binlogMap.get("attribute_id") != null &&
           binlogMap.containsKey("attribute_value_id") && binlogMap.get("attribute_value_id") != null &&
           binlogMap.containsKey("type") && binlogMap.get("type") != null ) {
            List<String> list = new ArrayList();
            list.add(binlogMap.get("sku_id").toString());
            ResponseBO<List<SkuAttributeRelationsResp>> resp = bigDataApiFeign.listBySkuId(list);
            Map<String, HashSet<String>> map = new HashMap();
            Map<String,String> skuidAndAttributeidTypeMap = new HashMap<>();
            if(resp != null){
                List<SkuAttributeRelationsResp> skuAttributeRelationsResps = (List<SkuAttributeRelationsResp>)resp.getData();
                if(skuAttributeRelationsResps != null && skuAttributeRelationsResps.size() > 0){
                    HashSet hashSet = null;
                    for(SkuAttributeRelationsResp skuAttributeRelationsResp : skuAttributeRelationsResps){
                        if(map.containsKey(skuAttributeRelationsResp.getAttributeId())){
                            hashSet = map.get(skuAttributeRelationsResp.getAttributeId());
                            hashSet.add(skuAttributeRelationsResp.getAttributeValueId());
                        }else{
                            hashSet = new HashSet();
                            hashSet.add(skuAttributeRelationsResp.getAttributeValueId());
                            map.put(skuAttributeRelationsResp.getAttributeId(),hashSet);
                        }
                        skuidAndAttributeidTypeMap.put(binlogMap.get("sku_id").toString() + "." + skuAttributeRelationsResp.getAttributeId() ,skuAttributeRelationsResp.getType().toString() );
                    }
                }
            }
            List<Map<String,Object>> listMap = new ArrayList();
            Set<Map.Entry<String, HashSet<String>>> entries = map.entrySet();
            for (Map.Entry<String, HashSet<String>> entry : entries) {
                Map<String,Object> mapTemp = new HashMap<String, Object>();
                mapTemp.put("attributeType", skuidAndAttributeidTypeMap.get(binlogMap.get("sku_id").toString() + "." + entry.getKey()));
                mapTemp.put("attributeId", entry.getKey());
                try {
                    ResponseBO<AttributeManagementDetailResp> attributeManagementDetailResp =  bigDataApiFeign.getAttribute(entry.getKey());
                    if(attributeManagementDetailResp != null && attributeManagementDetailResp.getData() != null){
                        String name = attributeManagementDetailResp.getData().getAttributeName();
                        mapTemp.put("attributeName", name);
                    }
                }catch (Exception e) {
                    log.info("SkuAttributeRelations.reSetValue,Exception,bigDataApiFeign.getAttribute.");
                    e.printStackTrace();
                }
                HashSet<String> hashSet = entry.getValue();
                List<Map<String,Object>> listSub = new ArrayList();
                for(String s: hashSet){
                    try {
                        ResponseBO<AttributeValueResp> attributeValueResp = bigDataApiFeign.getAttributeValue(s);
                        if(attributeValueResp != null && attributeValueResp.getData() != null){
                            AttributeValueResp respSub = attributeValueResp.getData();
                            Map<String,Object> valueMap = new HashMap<>();
                            valueMap.put("attributeValueId",respSub.getId());
                            valueMap.put("attributeValueName",respSub.getValueName());
                            listSub.add(valueMap);
                        }
                    }catch (Exception e) {
                        log.info("SkuAttributeRelations.reSetValue,Exception:bigDataApiFeign.getAttributeValue1.");
                        e.printStackTrace();
                    }
                }
                mapTemp.put("attributeValue", JSONUtil.toJSONString(listSub));
                listMap.add(mapTemp);
            }
            esMap.put("attributeMap",JSONUtil.toJSONString(listMap));
        }
        System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
        return esMap;
    }


    /**
     * 因为商品和属性是一对多的关系，索引里这些字段目前不用：attributeCode，attributeName，value_Name，attributeId，attributeValueId，attributeType
     *  attributeMap 字段格式如下：
     * [{
     *      "attributeType": "1",
     *      "attributeId": "1252129898609266689",
     *      "attributeName": "颜色",
     *      "attributeValue": [{"attributeValueId":"1252129898630238210","attributeValueName":"红"},{"attributeValueId":"1252129898651209730","attributeValueName":"蓝"}]
     * }]
     */
    public Map<String, Object> reSetSpuValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("spu_id") && binlogMap.get("spu_id") != null ) {
            List<Map<String,Object>> listMap = new ArrayList();
            SpuAttributeRelationsReq req = new SpuAttributeRelationsReq();
            req.setSpuId(binlogMap.get("spu_id").toString());
            ResponsePageBO<SpuAttributeVO> spuAttributeVOs = bigDataApiFeign.listSpuAttributeBySpuId(req);
            for(int i = 0; i < 1; i ++){
                Map<String,Object> mapTemp = new HashMap<String, Object>();
                mapTemp.put("attributeType", 1);
                mapTemp.put("attributeId", 1);
                //mapTemp.put("attributeName", name);
                //List<Map<String,Object>> listSub = new ArrayList();
                //for(int j = 0; j< 1; j ++){
                //    Map<String,Object> valueMap = new HashMap<>();
                //    valueMap.put("attributeValueId",respSub.getId());
                //    valueMap.put("attributeValueName",respSub.getValueName());
                //    listSub.add(valueMap);
                //}
                //mapTemp.put("attributeValue", JSONUtil.toJSONString(listSub));
                listMap.add(mapTemp);
            }
            esMap.put("spuAttributeMap",JSONUtil.toJSONString(listMap));
        }
        System.out.println("------------SkuAttributeRelations.reSetSpuValue.json:"+esMap);
        return esMap;
    }





}
