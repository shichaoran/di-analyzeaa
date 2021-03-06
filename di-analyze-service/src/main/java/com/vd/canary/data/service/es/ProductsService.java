package com.vd.canary.data.service.es;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.CategoryReq;
import com.vd.canary.data.api.request.es.ProductDetailsReq;
import com.vd.canary.data.api.request.es.ProductsReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.CategoryRes;
import com.vd.canary.data.api.response.es.ProductSpuInfoResponse;
import com.vd.canary.data.api.response.es.ProductsRes;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.IOException;

public interface ProductsService {

    ResponseBO<ProductsRes> getProductsByKey(@RequestBody @Valid ProductsReq productsReq) throws Exception;

    ResponseBO<ProductsRes> getProductByCategory(@RequestBody @Valid ThreeCategoryReq threeCategoryReq) throws Exception;

    ResponseBO<ProductSpuInfoResponse>  getProductsDetail(@RequestBody @Valid ProductDetailsReq productDetailsReq) throws IOException;

    ResponseBO<CategoryRes> categoryRes(@RequestBody @Valid CategoryReq categoryReq);
}
