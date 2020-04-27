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
     * 商品二级目录
     * 商品三级目录
     */
    //private String OneCategoryNameF;
    //private String TwoCategoryNameF;
    //private String ThreeCategoryNameF;
    private String oneFrontCategory;

    private String twoFrontCategory;

    private String threeFrontCategory;


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

    // 会员级别，用来过滤议价  目前未使用(prive)  60尊享会员(vipPrice)  40普通会员&50vip会员(referencePrice)
    private String memberLevel;
}