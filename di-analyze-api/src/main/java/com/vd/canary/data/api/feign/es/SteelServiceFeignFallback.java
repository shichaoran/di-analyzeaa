package com.vd.canary.data.api.feign.es;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.core.exception.BusinessException;
import com.vd.canary.data.api.request.es.SteelReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.SteelRes;
import feign.hystrix.FallbackFactory;

import javax.validation.Valid;

import static com.vd.canary.core.constant.HttpResponseStatus.FEIGN_EXCEPTION;



public class SteelServiceFeignFallback implements FallbackFactory<SteelServiceFeign> {

    @Override
    public SteelServiceFeign create(Throwable e) {
        return new SteelServiceFeign() {
            @Override
            public ResponseBO<SteelRes> getProductByCategory(@Valid SteelReq steelReq) {
                throw new BusinessException(FEIGN_EXCEPTION).append(e.getMessage());
            }
        };
    }
}
