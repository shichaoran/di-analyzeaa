package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.core.bo.ResponsePageBO;
import com.vd.canary.core.bo.ResponsePageVO;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;

import com.vd.canary.obmp.customer.api.feign.agreement.AgreementFeignClient;
import com.vd.canary.obmp.customer.api.response.agreement.AgreementInfoVO;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ProtocolAgreement implements Function {
    @Autowired
    private ShopESServiceImpl shopESServiceImplTemp;
    @Override
    public void performES(String msg) {

        log.info("ProtocolAgreement.msg" + msg);
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
        if(binlogMap.containsKey("booth_code")) esMap.put("boothCode",binlogMap.get("booth_code"));
        if(binlogMap.containsKey("sign_date")) esMap.put("boothScheduledTime",binlogMap.get("sign_date"));
        if(binlogMap.containsKey("member_order")) esMap.put("memberOrder",binlogMap.get("member_order"));

        System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
        return esMap;
    }

}
