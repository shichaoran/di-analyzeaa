package com.vd.canary.data.service.es;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.SteelReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.SteelRes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @Author WangRuilin
 * @Date 2020/4/19 14:46
 */
public interface SteelService {

    @PostMapping("/data/steel/getProductByCategory")
    ResponseBO<SteelRes> getProductByCategory(@RequestBody @Valid SteelReq steelReq);
}
