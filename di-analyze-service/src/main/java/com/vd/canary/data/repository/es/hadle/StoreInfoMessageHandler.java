package com.vd.canary.data.repository.es.hadle;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.data.util.HttpClientUtils;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreInfoVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->StoreInfoHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 12:30
 **/
@Slf4j
@Component
public class StoreInfoMessageHandler extends BaseMessageHandler implements BaseHandler {

    public final static String         DATA_BASE    = "obmp_customer";
    public final static String         T_CAR = "store_info";

    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;
    @Autowired
    private HttpClientUtils httpClientUtils;
    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        StoreInfoVO storeInfoVO=null;
        long curTime = System.currentTimeMillis();
        try {
            storeInfoVO = getRawData(data, DATA_BASE, T_CAR, StoreInfoVO.class);
            if(storeInfoVO==null){
                return;
            }
            log.info("处理店铺信息信息：customerInfoVO="+storeInfoVO);
            String id = storeInfoVO.getId();
            StoreDataQueryReq storeDataQueryReq=new StoreDataQueryReq();
            storeDataQueryReq.setStoreId(id);
            StoreDataInfoResp storeDataInfoResp = httpClientUtils.getStoreDataInfoResp(storeDataQueryReq);
            ShopTO shopTO = shopDataHandler.assembleShopTo(storeDataInfoResp);
            if(Constant.INSERT.equals(type)){
                shopESService.saveShop(shopTO);
            }
            if(Constant.UPDATE.equals(type)){
                shopESService.updateShop(shopTO);
            }
            if(Constant.DELETE.equals(type)){
                shopESService.deletedShopById(shopTO.getId());
            }
        } catch (Exception e) {
            log.error("处理店铺信息信息{}data=" + data + ",storeInfoVO=" + storeInfoVO, e);
        }finally {
            log.info("StoreInfoMessageHandler--处理店铺信息信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }
}
