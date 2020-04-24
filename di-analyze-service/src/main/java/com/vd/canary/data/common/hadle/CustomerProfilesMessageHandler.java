package com.vd.canary.data.common.hadle;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.customer.vo.CustomerProfilesVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->CustomerProfilesMessageHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-24 21:31
 **/
@Slf4j
@Component
public class CustomerProfilesMessageHandler extends BaseMessageHandler implements BaseHandler {
    public final static String         DATA_BASE    = "obmp_customer";
    public final static String         T_CAR = "customer_profiles";



    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;


    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        CustomerProfilesVO customerProfilesVO=null;
        long curTime = System.currentTimeMillis();
        try {
            customerProfilesVO = getRawData(data, DATA_BASE, T_CAR, CustomerProfilesVO.class);
            if(customerProfilesVO==null){
                return;
            }
            log.info("处理客户档案信息：customerProfilesVO="+customerProfilesVO);
            String customerId = customerProfilesVO.getId();
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
            log.error("处理客户档案信息{}data=" + data + ",customerProfilesVO=" + customerProfilesVO, e);
        }finally {
            log.info("CustomerProfilesMessageHandler--处理客户档案信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }
}
