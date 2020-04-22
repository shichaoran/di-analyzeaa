package com.vd.canary.data.repository.es.hadle;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.data.util.HttpClientUtils;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreMediaVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->StoreMediaMessageHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 13:12
 **/
@Slf4j
@Component
public class StoreMediaMessageHandler extends BaseMessageHandler implements BaseHandler {
    public final static String         DATA_BASE    = "obmp_customer";
    public final static String         T_CAR = "store_media";

    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;
    @Autowired
    private HttpClientUtils httpClientUtils;
    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        StoreMediaVO storeMediaVO=null;
        long curTime = System.currentTimeMillis();
        try {
            storeMediaVO = getRawData(data, DATA_BASE, T_CAR, StoreMediaVO.class);
            if(storeMediaVO==null){
                return;
            }
            log.info("店铺图片视频信息：storeMediaVO="+storeMediaVO);
            String storeTemplateId = storeMediaVO.getStoreTemplateId();
            StoreDataQueryReq storeDataQueryReq=new StoreDataQueryReq();
            storeDataQueryReq.setStoreTemplateId(storeTemplateId);
            StoreDataInfoResp storeDataInfoResp = httpClientUtils.getStoreDataInfoResp(storeDataQueryReq);
            ShopTO shopTO = shopDataHandler.assembleShopTo(storeDataInfoResp);
            if(shopTO!=null) {
                if (Constant.UPDATE.equals(type) || Constant.DELETE.equals(type)) {
                    shopESService.updateShop(shopTO);
                }
            }
        } catch (Exception e) {
            log.error("店铺图片视频信息{}data=" + data + ",storeMediaVO=" + storeMediaVO, e);
        }finally {
            log.info("StoreMediaMessageHandler--店铺图片视频信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }
}
