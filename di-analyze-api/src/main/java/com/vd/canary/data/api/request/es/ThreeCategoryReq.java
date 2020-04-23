package com.vd.canary.data.api.request.es;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class ThreeCategoryReq extends ProductsBaseReq {

    /**
     * 商品一级目录code
     */
    private String fOneCategoryCode;
    /**
     * 商品二级目录code
     */
    private String fTwoCategoryCode;

    /**
     * 商品三级目录code
     */
    private String fThreeCategoryCode;
}
