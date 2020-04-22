package com.vd.canary.data.common.es.model;

import com.vd.canary.data.api.response.es.ShopProductRes;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ShopTO implements Serializable {

    private String id; //店铺id
    private String name; //店铺名称
    private String boothCode;//展厅编号
    private String mediaUrl; //多媒体地址
    private List<String> businessCategory; //类目
    private List<String> businessBrand;//品牌
    private String businessArea;//区域
    //private String imageOrder;
    //private String imageName; //名
    private String imageBanerJson;
    private List<ShopProductRes> shopProductRes;
    private String customerId;  // 客户·ID
    private String storeTemplateId; //模板id
    private String mainProducts;  //主营产品 -> 来自企业档案
    private String mainCategory; //主营类目（经营类目） -> 来自企业档案
    private LocalDateTime boothScheduledTime; //入驻时间
    private String level; //会员等级
    private String remark1;
    private String remark2;

    public ShopTO() {

    }
}