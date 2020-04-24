package com.vd.canary.data.api.response.es;

//import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author shichaoran
 * @Date 2020/4/9 20:33
 * @Version
 */
@Data
@ToString
@Accessors(chain = true)
public class ShopProductRes implements Serializable {
    private static final long serialVersionUID = 3638243643721964455L;
    /**
     * 商品Id
     */
    private String skuId;
    /**
     * 商品名称
     */
    private String skuName;
    /**
     * 商品图片地址
     */
    private String skuPic;
    /**
     * 商品价格
     */
    private String skuPrice;
    /**
     * 商品标题
     */
    private String skuTitle;
    /**
     * 商品副标题
     */
    private String skuSubtitle;
    /**
     * 商品计价单位
     */
    private String unit;
    /**
     * 价格类型
     */
    private String priceType;

    private LocalDateTime crateDate;

    private String proSkuSpuId;


}