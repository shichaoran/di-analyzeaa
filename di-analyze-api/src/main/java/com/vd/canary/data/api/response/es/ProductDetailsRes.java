package com.vd.canary.data.api.response.es;

//import com.alibaba.fastjson.JSON;
import lombok.*;
import lombok.experimental.Accessors;
import org.w3c.dom.Text;

import java.io.Serializable;
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
public class ProductDetailsRes implements Serializable {


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



/*    //店铺名称
    private String storeInfoName;
    //主营类目
    private String businessCategory;
    //主营产品
    private String mainProducts;
    //所在地区
    private String businessArea;
    //展厅编号
    private List<String> boothBusinessBoothCode;
    //会员等级
    private String customerProfilesLevel;
    //认证信息
    private String approveState;
    //供方类别
    private String enterpriseType;
    //店铺二维码
    private String storeInfoStoreQrCode;
    //创建时间
    private Date gmtCreateTime;*/


}
