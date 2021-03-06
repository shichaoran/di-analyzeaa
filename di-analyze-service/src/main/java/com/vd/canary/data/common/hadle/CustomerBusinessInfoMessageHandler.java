package com.vd.canary.data.common.hadle;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.customer.vo.CustomerBusinessInfoVO;
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
    public final static String         T_CAR = "customebusiness_info";



    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;


    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        CustomerBusinessInfoVO customerBusinessInfoVO=null;
        long curTime = System.currentTimeMillis();
        try {
            customerBusinessInfoVO = getRawData(data, DATA_BASE, T_CAR, CustomerBusinessInfoVO.class);
            if(customerBusinessInfoVO==null){
                return;
            }
            log.info("处理客户经营信息：customerBusinessInfoVO="+customerBusinessInfoVO);
            String customerId = customerBusinessInfoVO.getCustomerId();
            Map<String, Object> stringObjectMap = shopESService.boolQueryByCustomerId(customerId);
            String shopId = (String)stringObjectMap.get("id");
            StoreDataQueryReq storeDataQueryReq=new StoreDataQueryReq();
            storeDataQueryReq.setStoreId(shopId);
            ShopTO shopTO = shopDataHandler.assembleShopTo(storeDataQueryReq);
            if(shopTO!=null) {
                if (Constant.UPDATE.equals(type)) {
                    shopESService.updateShop(shopTO);
                }
            }
        } catch (Exception e) {
            log.error("处理客户经营信息{}data=" + data + ",customerBusinessInfoVO=" + customerBusinessInfoVO, e);
        }finally {
            log.info("CustomerBusinessInfoMessageHandler--处理客户经营信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }


}
