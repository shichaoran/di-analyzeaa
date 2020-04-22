package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.data.util.JSONUtils;
import com.vd.canary.obmp.customer.api.feign.agreement.AgreementFeignClient;
import com.vd.canary.obmp.customer.api.feign.booth.BoothBusinessFeignClient;
import com.vd.canary.obmp.customer.api.feign.customer.CustomerClient;
import com.vd.canary.obmp.customer.api.feign.data.DataFeignClient;
import com.vd.canary.obmp.customer.api.feign.store.StoreInfoFeignClient;
import com.vd.canary.obmp.customer.api.feign.store.StoreLoopBannerFeignClient;
import com.vd.canary.obmp.customer.api.feign.store.StoreMediaFeignClient;
import com.vd.canary.obmp.customer.api.request.booth.BoothBusinessReq;
import com.vd.canary.obmp.customer.api.request.customer.CustomerBaseBusinessReq;
import com.vd.canary.obmp.customer.api.response.agreement.AgreementVO;
import com.vd.canary.obmp.customer.api.response.booth.BoothBusinessInfoResp;
import com.vd.canary.obmp.customer.api.response.customer.vo.CustomerBusinessInfoVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreLoopBannerVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreMediaVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreTemplateVO;
import com.vd.canary.obmp.product.api.response.spu.ProductSpuDetailResp;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;


@Slf4j
@Component
public class StoreInfo implements Function {
    private static final Logger logger = LoggerFactory.getLogger(StoreInfo.class);

    /**
     * 通过店铺->coustemer->展位编号
     */
    @Autowired
    private ShopESServiceImpl shopESServiceImplTemp;
    @Autowired
    private DataFeignClient dataFeignClient;


    @Override
    public void performES(String msg) {
        logger.info("StoreInfo.msg" + msg);
        if (StringUtils.isEmpty(msg)) {
            return;
        }
        HashMap hashMap = JSON.parseObject(msg, HashMap.class);
        String type = (String) hashMap.get("type");
        String id = "";

        HashMap bnglogMap = null;
        if (hashMap.containsKey("info")) {
            bnglogMap = JSON.parseObject(hashMap.get("info").toString(), HashMap.class);
        }

//        ShopESServiceImpl shopESServiceImplTemp = new ShopESServiceImpl();
        ShopTO shopTO = null;

        if (type.equals("insert")) {
            try {
                Map<String, Object> esMap = new HashMap();
                Map<String, Object> resjson = reSetValue(esMap, bnglogMap);
                shopESServiceImplTemp.saveShop(JSONObject.toJSONString(resjson),bnglogMap.get("id").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equals("update")) {

            id = bnglogMap.get("id").toString();
            try {
                Map<String, Object> esMap = shopESServiceImplTemp.findById(id);
                if (esMap != null) {
                    Map<String,Object> resjson = reSetValue(esMap, bnglogMap);
                    shopESServiceImplTemp.updateShop(resjson);
                } else{
                Map<String, Object> esMapT = new HashMap();
                Map<String, Object> resjson = reSetValue(esMapT, bnglogMap);
                    shopESServiceImplTemp.saveShop(JSONObject.toJSONString(resjson),bnglogMap.get("id").toString());
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("delete")) {
            try {
//                shopESServiceImplTemp.deletedShopById(shopTO.getId());
                shopESServiceImplTemp.deletedShopById(bnglogMap.get("id").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, Object> reSetValue(Map<String ,Object> esMap, Map<String,Object> binlogMap) {

        Set<Map.Entry<String, Object>> entries = binlogMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            if (entry.getKey().equals("id")) {
                esMap.put("id", entry.getValue());
                try {
                    String id = entry.getValue().toString();
                    ResponseBO<StoreTemplateVO> res = dataFeignClient.queryStoreTemplate(id);
                    log.info("StoreInfo.reSetValue,storeTemplateId.res={}.", JSONUtil.toJSON(res).toJSONString());
                    if (res != null) {
                        StoreTemplateVO pro = (StoreTemplateVO) res.getData();
                        log.info("StoreInfo.reSetValue,storeTemplateId.pro={}.", JSONUtil.toJSON(pro).toJSONString());
                        if (pro != null && pro.getTemplateStatus().equals("enable")) {
                            esMap.put("storeTemplateId", pro.getId());

                            String storeTemplateId = pro.getId();
                            ResponseBO<List<StoreLoopBannerVO>> res1 = dataFeignClient.queryStoreLoopBannerByTemplateId(storeTemplateId);
                            log.info("StoreInfo.reSetValue,storeTemplateId.res={}.", JSONUtil.toJSON(res).toJSONString());
                            if (res1 != null) {
                                List<StoreLoopBannerVO> pro1 = (List<StoreLoopBannerVO>) res.getData();
                                log.info("StoreInfo.reSetValue,StoreLoopBannerVO.pro={}.", JSONUtil.toJSON(pro1).toJSONString());
                                if (pro != null && pro1.size() > 0) {
                                    for (int i=0;i<pro1.size();i++){
                                        esMap.put("imageOrder",pro1.get(i).getImageOrder());
                                        esMap.put("imageName",pro1.get(i).getImageName());
                                        esMap.put("imageUrl",pro1.get(i).getImageUrl());
                                    }
                                }
                            }
                            ResponseBO<List<StoreMediaVO>> res2 = dataFeignClient.queryStoreMediaByTemplateId(storeTemplateId);
                            log.info("StoreInfo.reSetValue,storeTemplateId.res={}.", JSONUtil.toJSON(res).toJSONString());
                            if (res1 != null) {
                                List<StoreMediaVO> pro2 = (List<StoreMediaVO>) res.getData();
                                log.info("StoreInfo.reSetValue,StoreMediaVO.pro={}.", JSONUtil.toJSON(pro2).toJSONString());
                                if (pro != null && pro2.size() > 0) {
                                    for (int i=0;i<pro2.size();i++){
                                        esMap.put("mediaUrl",pro2.get(i).getMediaUrl());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.info("StoreInfo.reSetValue,Exception:dataFeignClient.storeTemplateId.");
                    e.printStackTrace();
                }

            }
            if (entry.getKey().equals("name")) esMap.put("name", entry.getValue());
            if (entry.getKey().equals("customer_id")) {
                esMap.put("customerId", entry.getValue());

                try {
                    String id = entry.getValue().toString();
                    ResponseBO<AgreementVO> res = dataFeignClient.getMemberInfo(id);
                    log.info("StoreInfo.reSetValue,customer_id.res={}.",JSONUtil.toJSON(res).toJSONString());
                    if(res != null){
                        AgreementVO pro = (AgreementVO) res.getData();
                        log.info("StoreInfo.reSetValue,AgreementVO.pro={}.",JSONUtil.toJSON(pro).toJSONString());
                        if(pro != null){
                            esMap.put("memberOrder",pro.getMemberOrder());
                            esMap.put("boothCode",pro.getBoothCode());
                            esMap.put("boothScheduledTime",pro.getSignDate());
                        }
                    }

                    CustomerBaseBusinessReq customerBaseBusinessReq = new CustomerBaseBusinessReq();
                    customerBaseBusinessReq.setCustomerId(id);
                    ResponseBO<CustomerBusinessInfoVO>  res1 = dataFeignClient.getBasicBusinessInfo(customerBaseBusinessReq);
                    log.info("StoreInfo.reSetValue,spu_id.res={}.",JSONUtil.toJSON(res).toJSONString());
                    if(res != null){
                        CustomerBusinessInfoVO pro1 = (CustomerBusinessInfoVO) res1.getData();
                        log.info("StoreInfo.reSetValue,AgreementVO.pro={}.",JSONUtil.toJSON(pro1).toJSONString());
                        if(pro1 != null){
                            esMap.put("businessCategory",pro1.getBusinessCategory());
                            List<String> lists = new ArrayList<>();
                            lists.add(pro1.getBusinessBrand());
                            esMap.put("businessBrand",lists);
                            esMap.put("businessArea",pro1.getMainProducts());
                            esMap.put("mainProducts",pro1.getBusinessArea());
                        }
                    }

                }catch (Exception e) {
                    log.info("StoreInfo.reSetValue,Exception:dataFeignClient.getMemberInfo.");
                    e.printStackTrace();
                }





            }
        }
        System.out.println("------------reSetValue.json:" + esMap);
        return esMap;
    }


}

