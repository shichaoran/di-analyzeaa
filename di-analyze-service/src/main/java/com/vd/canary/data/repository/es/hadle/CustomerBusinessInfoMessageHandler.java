package com.vd.canary.data.repository.es.hadle;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.util.HttpClientUtils;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.agreement.CustomerInfoVO;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->CustomerBusinessInfoHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 13:10
 **/
@Slf4j
@Component
public class CustomerBusinessInfoMessageHandler extends BaseMessageHandler implements BaseHandler {
    public final static String         DATA_BASE    = "obmp_customer";
    public final static String         T_CAR = "customer_business_info";

    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;

    @Autowired
    private HttpClientUtils httpClientUtils;
    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        CustomerInfoVO customerInfoVO=null;
        long curTime = System.currentTimeMillis();
        try {
            customerInfoVO = getRawData(data, DATA_BASE, T_CAR, CustomerInfoVO.class);
            if(customerInfoVO==null){
                return;
            }
            log.info("处理客户经营信息：customerInfoVO="+customerInfoVO);
            String customerId = customerInfoVO.getCustomerId();
            Map<String, Object> stringObjectMap = shopESService.boolQueryByCustomerId(customerId);
            String shopId = (String)stringObjectMap.get("id");
            StoreDataQueryReq storeDataQueryReq=new StoreDataQueryReq();
            storeDataQueryReq.setStoreId(shopId);
            StoreDataInfoResp storeDataInfoResp = httpClientUtils.getStoreDataInfoResp(storeDataQueryReq);
            ShopTO shopTO = shopDataHandler.assembleShopTo(storeDataInfoResp);
            if(shopTO!=null) {
                if (type.equals("update") || type.equals("delete")) {
                    shopESService.updateShop(shopTO);
                }
            }
        } catch (Exception e) {
            log.error("处理客户经营信息{}data=" + data + ",customerInfoVO=" + customerInfoVO, e);
        }finally {
            log.info("CarMessageHandler--处理客户经营信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }


}
