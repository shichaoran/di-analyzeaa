package com.vd.canary.data.controller.es;

import com.vd.canary.core.bo.ResponseBO;
import com.vd.canary.data.api.request.es.SteelReq;
import com.vd.canary.data.api.request.es.ThreeCategoryReq;
import com.vd.canary.data.api.response.es.ProductsRes;
import com.vd.canary.data.api.response.es.SteelRes;
import com.vd.canary.data.common.es.model.FinalSteel;
import com.vd.canary.data.service.es.ProductsService;
import com.vd.canary.data.service.es.SteelService;
import com.vd.canary.service.controller.BaseController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Author WangRuilin
 * @Date 2020/4/19 14:43
 */

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SteelController extends BaseController {

    private final SteelService steelService;
    @PostMapping("/data/steel/getProductByCategory")
    ResponseBO<SteelRes> getProductByCategory(@RequestBody @Valid SteelReq steelReq) throws Exception{
        ResponseBO<SteelRes> res = steelService.getProductByCategory(steelReq);
        return res;
    }
}
