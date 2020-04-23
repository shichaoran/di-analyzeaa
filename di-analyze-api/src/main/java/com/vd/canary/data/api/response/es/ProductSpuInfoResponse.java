package com.vd.canary.data.api.response.es;

//import com.alibaba.fastjson.JSON;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSpuInfoResponse implements Serializable {

    private String spuId;
    private String spuName;
    private String spuPic;
    private String spuTitle;
    private List<ProductSkuInfoVO> productSkuInfoVO;

}
