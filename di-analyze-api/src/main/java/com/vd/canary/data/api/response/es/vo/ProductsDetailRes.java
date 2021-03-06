package com.vd.canary.data.api.response.es.vo;

import lombok.*;
import lombok.experimental.Accessors;
import org.bouncycastle.util.StringList;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductsDetailRes implements Serializable {

    //skuid
    private String skuId;
    //skuname
    private String skuName;
    //sku标题
    private String proSkuTitle;
    //sku副标题
    private String proSkuSubTitle;
    //sku图片地址
    private String proSkuSkuPicJson;
    //商品属性
    private String attributeMapJson ;
    //商品定价
    private String skuSellPriceJson;
    //定价类型
    private Integer skuSellPriceType;
    //sku创建时间
    private  LocalDateTime skuGmtCreateTime;
    //辅助单位
    private String skuAuxiliaryUnit;
    //三级类目名称
    private String fThreeCategoryName;

    //店铺id
    private String shopId;
    //店铺名称
    private String storeInfoName;
    //主营类目
    private String businessCategory;
    //主营产品
    private String mainProducts;
    //所在地区
    private String businessArea;
    //展厅编号
    private String boothBusinessBoothCode; //List boothBusinessBoothCode;
    //会员等级
    private String customerProfilesLevel;
    //认证信息
    private String approveState;
    //供方类别
    private String enterpriseType;
    //店铺二维码
    private String storeInfoStoreQrCode;
    //创建时间
    private LocalDateTime gmtCreateTime;

    private LocalDateTime boothScheduledTime; //入驻时间

    //供货区域id
    private String regionalIdJson;
    //供货区域名称
    private String skuRegionalNameJson;

    //spuid
    private String proSkuSpuId;

    private String isGmProduct;

    private String shelvesState;

}
