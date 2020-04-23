package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct;

import com.alibaba.fastjson.JSON;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
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

@Slf4j
@Component
public class SkuSellingPrice implements Function {

    @Autowired
    private ProductESServiceImpl productESServiceImplTemp;

    @Autowired
    public ShopESServiceImpl shopESService;

    @Override
    public void performES(String msg) {
        log.info("SkuSellingPrice.msg" + msg);

        if(StringUtils.isEmpty(msg)){
            return;
        }
        HashMap hashMap = JSON.parseObject(msg, HashMap.class);
        String type = (String) hashMap.get("type");
        String skuid = null;
        String storeId = null;
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
                    storeId = esMap.get(storeId).toString();
                    log.info("SkuSellingPrice.performES,brand_id.esMap={}.", JSONUtil.toJSON(esMap).toJSONString());
                    if(esMap != null){
                        Map<String, Object> resjson = reSetValue(esMap, binlogMap);
                        productESServiceImplTemp.updateProduct(resjson);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 商品新增后同步店铺索引中的字段：businessCategory businessBrand skuPrice ...
        updateShopIndexProduct(skuid,storeId);
    }

    public Map<String, Object> reSetValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("price_type")) esMap.put("skuSellPriceType",binlogMap.get("price_type"));
        if(binlogMap.containsKey("price_json")) esMap.put("skuSellPriceJson",binlogMap.get("price_json"));
        System.out.println("------------SkuSellingPrice.reSetValue.json:"+esMap);
        return esMap;
    }

    public void updateShopIndexProduct(String skuid,String storeId){
        if(skuid == null || storeId == null){
            return;
        }
        //Map<String, Object> findById
        try {
            Map<String, Object> esShopMap = shopESService.findById(storeId);

            shopESService.updateShop(esShopMap);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


}
