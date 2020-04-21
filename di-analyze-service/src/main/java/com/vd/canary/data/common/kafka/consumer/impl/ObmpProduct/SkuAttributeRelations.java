package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.data.util.JSONUtils;
import com.vd.canary.obmp.product.api.feign.AttributeManagementFeign;
import com.vd.canary.obmp.product.api.feign.AttributeValueFeign;
import com.vd.canary.obmp.product.api.feign.BigDataApiFeign;
import com.vd.canary.obmp.product.api.feign.SkuAttributeRelationsFeign;
import com.vd.canary.obmp.product.api.request.category.foreground.CategoryRelationsReq;
import com.vd.canary.obmp.product.api.request.sku.SkuAttributeRelationsReq;
import com.vd.canary.obmp.product.api.response.attribute.AttributeManagementDetailResp;
import com.vd.canary.obmp.product.api.response.attribute.AttributeValueResp;
import com.vd.canary.obmp.product.api.response.brand.BrandManagementResp;
import com.vd.canary.obmp.product.api.response.category.CategoryRelationsResp;
import com.vd.canary.obmp.product.api.response.file.vo.FileManagementVO;
import com.vd.canary.obmp.product.api.response.spu.ProductSpuDetailResp;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
                        Map<String, Object> resjson = reSetValue(esMap, binlogMap);
                        productESServiceImplTemp.updateProduct(resjson);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 因为商品和属性是一对多的关系，索引里这些字段目前不用：attributeCode，attributeName，value_Name，attributeId，attributeValueId，attributeType
     * attributeMap字段格式如下：
     * [{
     *      "attributeType": "1",
     *      "attributeId": "1252129898609266689",
     *      "attributeName": "颜色",
     *      "attributeValue": [{"attributeValueId":"1252129898630238210","attributeValueName":"红"},{"attributeValueId":"1252129898651209730","attributeValueName":"蓝"}]
     * }]
     * @param esMap
     * @param binlogMap
     * @return
     */
    public Map<String, Object> reSetValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("attribute_id") && binlogMap.get("attribute_id") != null &&
            binlogMap.containsKey("attribute_value_id") && binlogMap.get("attribute_value_id") != null &&
            binlogMap.containsKey("type") && binlogMap.get("type") != null ) {
            Object attributeMapObject = esMap.get("attributeMap");
            if(attributeMapObject == null){
                List<Map<String,Object>> listMap = new ArrayList();
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("attributeType", binlogMap.get("type"));
                map.put("attributeId", binlogMap.get("attribute_id"));
                ResponseBO<AttributeManagementDetailResp> AttributeManagementDetailResp = bigDataApiFeign.getAttribute(binlogMap.get("attribute_id").toString());
                if(AttributeManagementDetailResp != null && AttributeManagementDetailResp.getData() != null){
                    map.put("attributeName", binlogMap.get(AttributeManagementDetailResp.getData().getAttributeName()));
                }
                ResponseBO<AttributeValueResp> attributeValueResp = bigDataApiFeign.getAttributeValue(binlogMap.get("attribute_value_id").toString());
                if(attributeValueResp != null && attributeValueResp.getData() != null){
                    AttributeValueResp resp = attributeValueResp.getData();
                    List<Map<String,Object>> list = new ArrayList();
                    Map<String,Object> valueMap = new HashMap<>();
                    valueMap.put("attributeValueId",resp.getId());
                    valueMap.put("attributeValueName",resp.getValueName());
                    list.add(valueMap);
                    map.put("attributeValue", JSONUtil.toJSONString(list));
                }
                listMap.add(map);
                esMap.put("attributeMap",JSONUtil.toJSONString(listMap));
            }else{
                JSONArray jsonArray = JSONArray.parseArray(attributeMapObject.toString());
                for(int i=0;i<jsonArray.size();i++){
                    Map o = (Map)jsonArray.get(i);
                    System.out.println(o.get("name") + " " + o.get("price"));
                }
            }
        }


        System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
        return esMap;
    }




}
