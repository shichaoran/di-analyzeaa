package com.vd.canary.data.repository.es.hadle;

import java.util.List;

import com.vd.canary.data.common.es.model.ImageBanerDTO;
import com.vd.canary.data.common.es.model.ShopTO;
import com.vd.canary.obmp.customer.api.response.agreement.AgreementVO;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;
import com.vd.canary.obmp.customer.api.response.customer.vo.CustomerBusinessInfoVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreInfoVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreLoopBannerVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreMediaVO;
import com.vd.canary.obmp.customer.api.response.customer.vo.store.StoreTemplateVO;
import com.vd.canary.utils.BeanUtil;
import com.vd.canary.utils.CollectionUtil;
import com.vd.canary.utils.ObjectUtil;

import org.springframework.stereotype.Component;

/**
 * @program: di-analyze->ShspDataHandler
 * @description:
 * @author: zcy
 * @create: 2020-04-22 13:42
 **/
@Component
public class ShopDataHandler {



    public ShopTO assembleShopTo(StoreDataInfoResp storeDataInfoResp){
        ShopTO shopTO =new ShopTO();
        try {
            StoreInfoVO storeInfoVO = storeDataInfoResp.getStoreInfoVO();
            AgreementVO agreementVO = storeDataInfoResp.getAgreementVO();
            CustomerBusinessInfoVO customerBusinessInfoVO = storeDataInfoResp.getCustomerBusinessInfoVO();
            List<StoreLoopBannerVO> storeLoopBannerVO = storeDataInfoResp.getStoreLoopBannerVO();
            StoreMediaVO storeMediaVO = storeDataInfoResp.getStoreMediaVO();
            StoreTemplateVO storeTemplateVO = storeDataInfoResp.getStoreTemplateVO();
            if(storeInfoVO!=null){
                shopTO.setId(storeInfoVO.getId());
                shopTO.setName(storeInfoVO.getName());
                shopTO.setCustomerId(storeInfoVO.getCustomerId()+"");
                shopTO.setBoothScheduledTime(storeInfoVO.getGmtCreateTime());
            }
            if(ObjectUtil.isNotEmpty(agreementVO)){
                shopTO.setBoothCode(agreementVO.getBoothCode());
                shopTO.setBoothCode(agreementVO.getBoothCode());
            }
            if(ObjectUtil.isNotEmpty(customerBusinessInfoVO)){
                shopTO.setBusinessArea(customerBusinessInfoVO.getBusinessArea());
                shopTO.setMainProducts(customerBusinessInfoVO.getMainProducts());
                shopTO.setMainCategory(customerBusinessInfoVO.getBusinessCategory());
            }
            if(CollectionUtil.isNotEmpty(storeLoopBannerVO)){
                List<ImageBanerDTO> imageBanerDTOS = BeanUtil.convert(storeLoopBannerVO, ImageBanerDTO.class);
                shopTO.setImageBanerJson(imageBanerDTOS);
            }
            if(ObjectUtil.isNotEmpty(storeTemplateVO)){
                shopTO.setMediaUrl(storeTemplateVO.getId());
            }
            if(ObjectUtil.isNotEmpty(storeMediaVO)){
                shopTO.setMediaUrl(storeMediaVO.getMediaUrl());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shopTO;
    }

}
