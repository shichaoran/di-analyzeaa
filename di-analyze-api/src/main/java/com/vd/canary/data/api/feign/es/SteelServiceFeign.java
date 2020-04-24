package com.vd.canary.data.api.feign.es;

import com.vd.canary.core.api.Feign;
import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.SteelReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.ProductsRes;
import com.vd.canary.data.api.response.es.SteelRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author WangRuilin
 * @Date 2020/4/19 14:16
 */



@FeignClient(value = "canary-dataanalyze",
        contextId = "steelFeign",
        decode404 = true,
        fallbackFactory = SteelServiceFeignFallback.class)
public interface SteelServiceFeign extends Feign {

    /**
     * 根据一级目录、二级目录和三级目录名称返回商品列表
     */
    @PostMapping("/data/steel/getProductByCategory")
    ResponseBO<SteelRes> getProductByCategory(@RequestBody @Valid SteelReq steelReq);
}
