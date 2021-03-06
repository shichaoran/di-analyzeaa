package com.vd.canary.data.api.response.es.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import com.vd.canary.data.api.response.es.ShopProductRes;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author shichaoran
 * @Date 2020/4/6 10:37
 * @Version
 */
@Data
@ToString
@Accessors(chain = true)
public class ShopVo implements Serializable {
    private static final long serialVersionUID = -8274283938195135641L;
    private String name; //店铺名称
    private String id; //店铺id
    private List<String> boothCode;//展厅编号
    private String mediaUrl; //多媒体地址
    private List<String> businessCategory; //经营类目
    private List<String> businessBrand;//品牌
    private String businessArea;//区域
    private List<ImageBanerVO> imageBanerVOS;
    private List<ShopProductRes> shopProductRes;
    private HashSet<String> classify;
    private String customerId;  // 客户·ID
    private String storeTemplateId; //模板id
    private String mainProducts;  //主营产品
    private List<String> mainCategory; //主营类目（经营类目） -> 来自企业档案
    private LocalDateTime boothScheduledTime; //入驻时间
    //    会员等级
    private String memberOrder;
    private String logoImageUrl;


}

