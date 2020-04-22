package com.vd.canary.data.api.response.es.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author WangRuilin
 * @Date 2020/4/19 13:56
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SteelVO implements Serializable {
    //spuid
    private String spuID;
    //spuname
    private String spuName;
    //商品属性 key为属性id+属性类型，value为属性值
//    private Map<String, Map<String,String>> attributeMap ;//Map<String, HashSet<String>> attributeMap;

    //spuid
    private String skuID;
    //spuname
    private String skuName;

    //商品定价
    private String skuSellPriceJson;
    //定价类型
    private Integer skuSellPriceType;
    //sku创建时间
    private LocalDateTime skuGmtCreateTime;
    //辅助单位
    private String skuAuxiliaryUnit;
//    //三级类目id
    private String fThreeCategoryId;
//    //三级类目编码
    private String fThreeCategoryCode;
    //二级类目名称
//    private String fTwoCategoryName;
}
