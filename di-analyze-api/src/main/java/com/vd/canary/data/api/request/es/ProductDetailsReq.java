package com.vd.canary.data.api.request.es;

import com.vd.canary.core.bo.RequestBO;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDetailsReq extends RequestBO {
    /**
     * 商品id
     */
    //@NotBlank(message = "spuId不能为空")
    private String spuId;

    //@NotBlank(message = "店铺名不能为空")
    private String storeId;

    private String skuId;
}
