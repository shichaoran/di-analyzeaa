package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.obmp.product.api.feign.RegionalManagementFeign;
import com.vd.canary.obmp.product.api.feign.SkuWarehouseRelationsFeign;
import com.vd.canary.obmp.product.api.feign.WarehouseManagementFeign;
import com.vd.canary.obmp.product.api.response.region.RegionalManagementResp;
import com.vd.canary.obmp.product.api.response.warehouse.WarehouseManagementDetailResp;
import com.vd.canary.obmp.product.api.response.warehouse.vo.SkuWarehouseRelationsVO;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class SkuWarehouseRelations implements Function {

    @Autowired
    private ProductESServiceImpl productESServiceImplTemp;

    @Override
    public void performES(String msg) {
        log.info("SkuWarehouseRelations.msg" + msg);
        if(StringUtils.isNotBlank(msg)){
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
                    log.info("SkuWarehouseRelations.performES,brand_id.esMap={}.", JSONUtil.toJSON(esMap).toJSONString());
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

    public Map<String, Object> reSetValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("warehouse_id")) esMap.put("warehouseId",binlogMap.get("warehouse_id"));
        if(binlogMap.containsKey("warehouse_name")) esMap.put("warehouseName",binlogMap.get("warehouse_name"));
        if(binlogMap.containsKey("inventory")) esMap.put("inventory",binlogMap.get("inventory"));
        if(binlogMap.containsKey("regional_id")) esMap.put("regionalId",binlogMap.get("regional_id"));
        if(binlogMap.containsKey("regional_name")) esMap.put("skuRegionalName",binlogMap.get("regional_name"));
        System.out.println("------------SkuWarehouseRelations.reSetValue.json:"+esMap);
        return esMap;
    }



}

