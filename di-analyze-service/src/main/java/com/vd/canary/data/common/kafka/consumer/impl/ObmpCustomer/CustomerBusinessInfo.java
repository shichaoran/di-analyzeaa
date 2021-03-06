package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;


import com.alibaba.fastjson.JSON;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author shichaoran
 * @Date 2020/4/9 16:49
 * @Version
 */

@Slf4j
@Component
public class CustomerBusinessInfo implements Function {

    @Autowired
    private ShopESServiceImpl shopESServiceImplTemp;
    @Override
    public void performES(String msg) {

        log.info("CustomerBusinessInfo.msg" + msg);
        if(StringUtils.isEmpty(msg)){
            return;
        }
        HashMap hashMap = JSON.parseObject(msg, HashMap.class);
        String type = (String) hashMap.get("type");
        String id = null;
        HashMap<String,Object> binlogMap = null;
        if(hashMap.containsKey("info")){
            binlogMap = JSON.parseObject(hashMap.get("info").toString(), HashMap.class);
        }
        if (type.equals("insert") || type.equals("update")) {
            if(binlogMap != null && binlogMap.size() > 0){
                id = binlogMap.get("customer_id").toString();
                try {
                    Map<String, Object> esMap = shopESServiceImplTemp.findById(id);
                    log.info("StoreTemplate.performES,storeTemplateId.esMap={}.", JSONUtil.toJSON(esMap).toJSONString());
                    if(esMap != null){
                        Map<String, Object> resjson = reSetValue(esMap, binlogMap);
                        shopESServiceImplTemp.updateShop(resjson);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Map<String, Object> reSetValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("business_category")) esMap.put("businessCategory",binlogMap.get("business_category"));

        if(binlogMap.containsKey("business_brand")) {
            List<String> list = new ArrayList<>();
            list.add(binlogMap.get("business_brand").toString());
            esMap.put("businessBrand",list);
        }
        if(binlogMap.containsKey("business_area")) esMap.put("businessArea",binlogMap.get("business_area"));
        if(binlogMap.containsKey("main_products")) esMap.put("mainProducts",binlogMap.get("main_products"));

        System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
        return esMap;
    }
}