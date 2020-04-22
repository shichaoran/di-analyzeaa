package com.vd.canary.data.api.request.es;

import com.vd.canary.core.bo.RequestPageBO;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class SteelReq extends RequestPageBO {


    /**
     * 商品一级目录
     */
    private String fOneCategoryName;
    /**
     * 商品二级目录
     */
    private String fTwoCategoryName;

    /**
     * 商品三级目录
     */
    private String fThreeCategoryName;
    /**
     * 品牌
     */
    private List<String> bBrandName;

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
    /**
     * 属性id列表
     */
    private Map<String, String> attributes;

    private List<String> spuNames;

    private List<SpecCommand> specCommands;
}