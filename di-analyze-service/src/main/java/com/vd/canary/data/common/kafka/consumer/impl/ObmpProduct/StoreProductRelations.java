package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct;

import com.alibaba.fastjson.JSON;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
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

@Slf4j
@Component
public class StoreProductRelations implements Function {

    @Autowired
    private ProductESServiceImpl productESServiceImplTemp;

    @Autowired
    private ShopESServiceImpl shopESServiceImpl;

    @Override
    public void performES(String msg) {

        log.info("StoreProductRelations.msg" + msg);
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
                    log.info("StoreProductRelations.performES,brand_id.esMap={}.", JSONUtil.toJSON(esMap).toJSONString());
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
        if(binlogMap.containsKey("store_id")){
            esMap.put("storeId",binlogMap.get("store_id"));
            try {
                Map<String, Object> shopEsRes = shopESServiceImpl.findById(binlogMap.get("store_id").toString());
                log.info("StoreProductRelations.performES,reSetValue.shopEsRes={}.", JSONUtil.toJSON(shopEsRes).toJSONString());
                if(shopEsRes != null){
                    esMap.put("storeId",shopEsRes.get("id"));
                    esMap.put("categoryId",""); // 暂时不好处理
                    esMap.put("storeName",shopEsRes.get("name"));
                    esMap.put("businessCategory",shopEsRes.get("businessCategory"));
                    esMap.put("mainProducts",shopEsRes.get("mainProducts"));
                    esMap.put("businessArea",shopEsRes.get("businessArea"));
                    esMap.put("boothBusinessBoothCode",shopEsRes.get("boothCode"));
                    esMap.put("customerProfilesLevel",shopEsRes.get("level"));
                    esMap.put("approveState","");
                    esMap.put("enterpriseType","");
                    esMap.put("storeInfoStoreQrCode","");
                    esMap.put("gmtCreateTime",shopEsRes.get("boothScheduledTime")); // 暂时用入驻时间
                    esMap.put("boothScheduledTime",shopEsRes.get("boothScheduledTime"));
                    esMap.put("logoImageUrl",shopEsRes.get("logoImageUrl"));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(binlogMap.containsKey("category_id")) esMap.put("categoryId",binlogMap.get("category_id"));
        System.out.println("------------StoreProductRelations.reSetValue.json:"+esMap);
        return esMap;
    }


}
