package com.vd.canary.data.api.request.es;

import com.vd.canary.core.bo.RequestPageBO;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class SteelReq extends RequestPageBO {


    /**
     * 商品一级目录id
     */
    private String fOneCategoryId;
    /**
     * 商品二级目录id
     */
    private String fTwoCategoryId;

    /**
     * 商品三级目录id
     */
    private String fThreeCategoryId;
    /**
     * 品牌id
     */
    private List<String> bBrandId;
    /**
     * price为desc 或者 asc
     */
    private String priceSort;

    /**
     * 是否议价
     */
    private String isDiscussPrice;

    /**
     * 配送区域
     */
    private String skuRegionalName;

}
