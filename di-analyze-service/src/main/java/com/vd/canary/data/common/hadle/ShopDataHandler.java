package com.vd.canary.data.common.hadle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.common.es.model.ImageBanerDTO;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.obmp.customer.api.feign.data.DataFeignClient;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.agreement.AgreementVO;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import com.vd.canary.obmp.customer.api.response.customer.vo.CustomerBusinessInfoVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.CustomerProfilesVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreInfoVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreLoopBannerVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreMediaVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreTemplateVO;
import com.vd.canary.utils.BeanUtil;
import com.vd.canary.utils.CollectionUtil;
import com.vd.canary.utils.LocalDateUtil;
import com.vd.canary.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->ShspDataHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 13:42
 **/
@Component
@Slf4j
public class ShopDataHandler {

      @Autowired
   private DataFeignClient dataFeignClient;

    public ShopTO assembleShopTo(StoreDataQueryReq storeDataQueryReq){
        StoreDataInfoResp storeDataInfoResp =new StoreDataInfoResp();
        try {
            ResponseBO<StoreDataInfoResp> storeDataInfoRespResponseBO = dataFeignClient.queryStoreDataInfo(storeDataQueryReq);
            if(storeDataInfoRespResponseBO.isFailed()){
                log.error("查询店铺数据信息{}storeDataQueryReq=" + storeDataQueryReq + ",storeDataInfoRespResponseBO=" + storeDataInfoRespResponseBO);
                return null;
            }
             storeDataInfoResp = storeDataInfoRespResponseBO.getData();
        }catch (Exception e){
            log.error("查询店铺数据信息{}storeDataQueryReq=" + storeDataQueryReq + ",e="+ e);
            return null;
        }

        ShopTO shopTO =new ShopTO();
        try {
            StoreInfoVO storeInfoVO = storeDataInfoResp.getStoreInfoVO();
            List<AgreementVO> agreementVOs = storeDataInfoResp.getAgreementVO();
            CustomerBusinessInfoVO customerBusinessInfoVO = storeDataInfoResp.getCustomerBusinessInfoVO();
            List<StoreLoopBannerVO> storeLoopBannerVOs = storeDataInfoResp.getStoreLoopBannerVO();
            StoreMediaVO storeMediaVO = storeDataInfoResp.getStoreMediaVO();
            StoreTemplateVO storeTemplateVO = storeDataInfoResp.getStoreTemplateVO();
            CustomerProfilesVO customerProfilesVO = storeDataInfoResp.getCustomerProfilesVO();
            if(storeInfoVO!=null){
                shopTO.setId(storeInfoVO.getId());
                shopTO.setName(storeInfoVO.getName());
                shopTO.setCustomerId(storeInfoVO.getCustomerId()+"");
                LocalDateTime gmtCreateTime = storeInfoVO.getGmtCreateTime();
                DateTimeFormatter df = DateTimeFormatter.ofPattern(LocalDateUtil.DEFAULT_PATTERN_TO_SECOND);
                String format = df.format(gmtCreateTime);
                shopTO.setBoothScheduledTime(format);
            }
            if(CollectionUtil.isNotEmpty(agreementVOs)){
                List<String> boothCodeList = agreementVOs.stream()
                                                   .map(AgreementVO::getBoothCode)
                                                   .collect(Collectors.toList());
                shopTO.setBoothCode(boothCodeList);
               if(agreementVOs.size()>1){
                   agreementVOs.sort(Comparator.comparing(AgreementVO::getGmtCreateTime));
               }
                shopTO.setMemberOrder(agreementVOs.get(0).getMemberOrder());
            }
            if(ObjectUtil.isNotEmpty(customerBusinessInfoVO)){
                shopTO.setBusinessArea(customerBusinessInfoVO.getBusinessArea());
                shopTO.setMainProducts(customerBusinessInfoVO.getMainProducts());
                shopTO.setMainCategory(customerBusinessInfoVO.getBusinessCategory());
            }
            if(CollectionUtil.isNotEmpty(storeLoopBannerVOs)){
                List<ImageBanerDTO> imageBanerDTOS = BeanUtil.convert(storeLoopBannerVOs, ImageBanerDTO.class);
                for (int i = 0; i <imageBanerDTOS.size() ; i++) {
                    StoreLoopBannerVO storeLoopBannerVO = storeLoopBannerVOs.get(i);
                    ImageBanerDTO imageBanerDTO = imageBanerDTOS.get(i);
                    imageBanerDTO.setImageOrder(storeLoopBannerVO.getImageOrder()+"");
                }
                shopTO.setImageBanerJson(imageBanerDTOS);
            }
            if(ObjectUtil.isNotEmpty(storeTemplateVO)){
                shopTO.setStoreTemplateId(storeTemplateVO.getId());
            }
            if(ObjectUtil.isNotEmpty(storeMediaVO)){
                shopTO.setMediaUrl(storeMediaVO.getMediaUrl());
            }
            if(ObjectUtil.isNotEmpty(customerProfilesVO)){
                shopTO.setLogoImageUrl(customerProfilesVO.getLogoImageUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shopTO;
    }
}
