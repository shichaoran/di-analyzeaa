package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;

import com.alibaba.fastjson.JSON;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author WangRuilin
 * @Date 2020/4/21 9:41
 */

@Slf4j
@Component
public class StoreTemplate implements Function {

    @Autowired
    private ShopESServiceImpl shopESServiceImplTemp;
    @Override
    public void performES(String msg) {

        log.info("StoreTemplate.msg" + msg);
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
                id = binlogMap.get("store_info_id").toString();
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
            if(binlogMap.containsKey("id") && binlogMap.get("template_status").equals("enable")) {
                esMap.put("storeTemplateId",binlogMap.get("id"));
            }
            System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
            return esMap;
        }



}
