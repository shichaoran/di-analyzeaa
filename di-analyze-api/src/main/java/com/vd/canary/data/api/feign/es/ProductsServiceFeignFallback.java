package com.vd.canary.data.api.feign.es;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.core.exception.BusinessException;
import com.vd.canary.data.api.request.es.*;
import com.vd.canary.data.api.response.es.CategoryRes;
import com.vd.canary.data.api.response.es.ProductSpuInfoResponse;
import com.vd.canary.data.api.response.es.ProductsRes;
import feign.hystrix.FallbackFactory;

import javax.validation.Valid;

import static com.vd.canary.core.constant.HttpResponseStatus.FEIGN_EXCEPTION;

public class ProductsServiceFeignFallback implements FallbackFactory<ProductsServiceFeign> {


    @Override
    public ProductsServiceFeign create(Throwable e) {
        return new ProductsServiceFeign() {
            @Override
            public ResponseBO<ProductsRes> getProductsByKey(@Valid ProductsReq productsReq) {
                throw new BusinessException(FEIGN_EXCEPTION).append(e.getMessage());
            }

            @Override
            public ResponseBO<CategoryRes> categoryres(@Valid CategoryReq categoryReq) {
                throw new BusinessException(FEIGN_EXCEPTION).append(e.getMessage());
            }


            @Override
            public ResponseBO<ProductSpuInfoResponse> getProductsDetail(@Valid ProductDetailsReq productDetailsReq) {
                throw new BusinessException(FEIGN_EXCEPTION).append(e.getMessage());
            }

            @Override
            public ResponseBO<ProductsRes> getProductByCategory(@Valid ThreeCategoryReq threeCategoryReq) {
                throw new BusinessException(FEIGN_EXCEPTION).append(e.getMessage());
            }



        };
    }
}
