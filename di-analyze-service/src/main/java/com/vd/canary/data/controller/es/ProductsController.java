package com.vd.canary.data.controller.es;

import java.io.IOException;

import javax.validation.Valid;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.CategoryReq;
import com.vd.canary.data.api.request.es.ProductDetailsReq;
import com.vd.canary.data.api.request.es.ProductsReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.CategoryRes;
import com.vd.canary.data.api.response.es.ProductSpuInfoResponse;
import com.vd.canary.data.api.response.es.ProductsRes;
import com.vd.canary.data.service.es.ProductsService;
import com.vd.canary.service.controller.BaseController;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProductsController extends BaseController {

    private final ProductsService productsService;




    @PostMapping("/data/products/getProductsByKey")
    public ResponseBO<ProductsRes> getProductsByKey(@RequestBody @Valid ProductsReq productsReq) throws Exception {
        ResponseBO<ProductsRes> res = productsService.getProductsByKey(productsReq);
        return res;
    }

    @PostMapping("/data/products/getProductByCategory")
    public ResponseBO<ProductsRes> getProductByCategory(@RequestBody @Valid ThreeCategoryReq threeCategoryReq) throws Exception {
        ResponseBO<ProductsRes> res = productsService.getProductByCategory(threeCategoryReq);
        return res;
    }

    @PostMapping("/data/products/getProductDetail")
    public ResponseBO<ProductSpuInfoResponse> getProductsDetail(@RequestBody @Valid ProductDetailsReq productDetailsReq) throws IOException {
        ResponseBO<ProductSpuInfoResponse> res = productsService.getProductsDetail(productDetailsReq);
        return res;
    }

    @PostMapping("/data/products/category")
    public ResponseBO<CategoryRes> categoryRes(@RequestBody @Valid CategoryReq categoryReq) {
        ResponseBO<CategoryRes> res = productsService.categoryRes(categoryReq);
        return res;
    }

}
