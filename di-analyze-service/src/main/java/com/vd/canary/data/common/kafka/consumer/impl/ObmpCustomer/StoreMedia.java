package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.response.es.vo.ShopVo;
import com.vd.canary.data.common.canal.KafkaService;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;

import com.vd.canary.obmp.customer.api.feign.store.StoreMediaFeignClient;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreMediaVO;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//import java.util.HashMap;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author shichaoran
 * @Date 2020/4/9 14:51
 * @Version
 */

@Slf4j
@Component
public class StoreMedia implements Function {
    @Autowired
    private ShopESServiceImpl shopESServiceImplTemp;
    @Override
    public void performES(String msg) throws IOException {

        log.info("StoreMedia.msg" + msg);
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
        ShopTO shopTO = null;
        if (type.equals("insert") || type.equals("update")) {
            if(binlogMap != null && binlogMap.size() > 0){
                id = binlogMap.get("store_template_id").toString();
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
        if(binlogMap.containsKey("media_url")) esMap.put("mediaUrl",binlogMap.get("media_url"));

        System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
        return esMap;
    }

}

