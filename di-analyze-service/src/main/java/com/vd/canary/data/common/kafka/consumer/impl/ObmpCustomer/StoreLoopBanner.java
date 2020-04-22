package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.response.es.vo.ShopVo;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.obmp.customer.api.feign.store.StoreLoopBannerFeignClient;
import com.vd.canary.obmp.customer.api.request.customer.bo.store.StoreLoopBannerBO;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author shichaoran
 * @Date 2020/4/16 15:21
 * @Version
 */

@Slf4j
@Component
public class StoreLoopBanner implements Function {

        @Autowired
        private ShopESServiceImpl shopESServiceImplTemp;
        @Override
        public void performES(String msg) {

            log.info("StoreLoopBanner.msg" + msg);
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
            if(binlogMap.containsKey("image_order")) esMap.put("imageOrder",binlogMap.get("image_order"));
            if(binlogMap.containsKey("image_name")) esMap.put("imageName",binlogMap.get("image_name"));
            if(binlogMap.containsKey("image_url")) esMap.put("imageUrl",binlogMap.get("image_url"));

            System.out.println("------------SkuAttributeRelations.reSetValue.json:"+esMap);
            return esMap;
        }
}
