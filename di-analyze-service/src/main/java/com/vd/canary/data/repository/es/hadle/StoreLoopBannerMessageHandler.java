package com.vd.canary.data.repository.es.hadle;

import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.constants.Constant;
import com.vd.canary.data.util.HttpClientUtils;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreLoopBannerVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->StoreLoopBannerMessageHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 13:12
 **/
@Slf4j
@Component
public class StoreLoopBannerMessageHandler extends BaseMessageHandler implements BaseHandler {
    public final static String         DATA_BASE    = "obmp_customer";
    public final static String         T_CAR = "store_loop_banner";

    @Autowired
    private ShopESServiceImpl shopESService;

    @Autowired
    private ShopDataHandler shopDataHandler;
    @Autowired
    private HttpClientUtils httpClientUtils;
    @Override
    public void handler(JSONObject data) {
        String type = data.getString("type");
        StoreLoopBannerVO storeLoopBannerVO=null;
        long curTime = System.currentTimeMillis();
        try {
            storeLoopBannerVO = getRawData(data, DATA_BASE, T_CAR, StoreLoopBannerVO.class);
            if(storeLoopBannerVO==null){
                return;
            }
            log.info("处理店铺轮播图信息：customerInfoVO="+storeLoopBannerVO);
            String storeTemplateId = storeLoopBannerVO.getStoreTemplateId();
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
            log.error("处理店铺轮播图信息{}data=" + data + ",storeLoopBannerVO=" + storeLoopBannerVO, e);
        }finally {
            log.info("StoreLoopBannerMessageHandler--处理店铺轮播图信息cost-time:{}",System.currentTimeMillis()-curTime);
        }
    }
}
