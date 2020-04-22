package com.vd.canary.data.repository.es.hadle;


import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.data.util.HttpClientUtils;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.agreement.AgreementVO;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->AgreementMessageHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 13:13
 **/
@Slf4j
@Component
public class ProtocolAgreementMessageHandler extends BaseMessageHandler implements BaseHandler {

    public final static String         DATA_BASE    = "obmp_customer";
    public final static String         T_CAR = "protocol_agreement";
    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;
    @Autowired
    private HttpClientUtils httpClientUtils;
    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        AgreementVO agreementVO=null;
        long curTime = System.currentTimeMillis();
        try {
            agreementVO = getRawData(data, DATA_BASE, T_CAR, AgreementVO.class);
            if(agreementVO==null){
                return;
            }
            log.info("处理车辆保险信息和保养信息：customerInfoVO="+agreementVO);
            String customerId = agreementVO.getCustomerId();
            Map<String, Object> stringObjectMap = shopESService.boolQueryByCustomerId(customerId);
            String shopId = (String)stringObjectMap.get("id");
            StoreDataQueryReq storeDataQueryReq=new StoreDataQueryReq();
            storeDataQueryReq.setStoreId(shopId);
            StoreDataInfoResp storeDataInfoResp = httpClientUtils.getStoreDataInfoResp(storeDataQueryReq);
            ShopTO shopTO = shopDataHandler.assembleShopTo(storeDataInfoResp);
            if(Constant.UPDATE.equals(type) || Constant.DELETE.equals(type)){
                shopESService.updateShop(shopTO);
            }
        } catch (Exception e) {
            log.error("处理协议合同表失败{}data=" + data + ",agreementVO=" + agreementVO, e);
        }finally {
            log.info("ProtocolAgreementMessageHandler--处理协议合同表信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }
}
