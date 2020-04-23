package com.vd.canary.data.api.response.es;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSkuInfoVO implements Serializable {

    //skuid
    private String skuId;
    //skuname
    private String skuName;
    //sku标题
    private String skuTitle;
    //sku副标题
    private String skuSubTitle;
    //商品属性
    private String attributeMapJson ;
    //商品价格
    private String priceJson;
    //价格类型
    private Integer priceType;
    //sku描述
    private String skuIntroduce;
    //sku图片地址
    private String proSkuSkuPicJson;
    //供货区域id
    private List<String> regionalId;
    //供货区域
    private List<String> regionalName;
    //仓库id
    private String warehouseId;
    //仓库名称
    private String warehouseName;
    //库存
    private String inventory;
}
