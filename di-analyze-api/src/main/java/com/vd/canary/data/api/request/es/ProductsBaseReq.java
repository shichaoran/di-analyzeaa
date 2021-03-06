package com.vd.canary.data.api.request.es;

import com.vd.canary.core.bo.RequestPageBO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author WangRuilin
 * @Date 2020/4/7 13:54
 */
@Setter
@Getter
public class ProductsBaseReq extends RequestPageBO  {
    /**
     * 品牌id
     */
    private List<String> productBrandName;
    /**
     * 三级类目名称
     */
    private List<String> frontThreeCategoryName;
    /**
     * 所在地
     */
    private List<String> businessAreaName;

    /**
     * price为desc 或者 asc
     */
    private String priceSort;

    /**
     * 是否议价 0-包含议价的商品，1-不含议价的商品
     */
    private String isDiscussPrice;

    /**
     * 是否入驻 0-全部商品，1-入驻展厅的商品
     */
    private String isHaveHouse;

    // 会员级别，用来过滤议价  目前未使用(prive)  60尊享会员(vipPrice)  40普通会员&50vip会员(referencePrice)
    private String memberLevel;
}
