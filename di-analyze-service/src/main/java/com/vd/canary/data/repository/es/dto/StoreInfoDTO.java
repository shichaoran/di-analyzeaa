package com.vd.canary.data.repository.es.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: di-analyze->StoreInfoDTO
 * @description:
 * @author: zcy
 * @create: 2020-04-23 09:19
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreInfoDTO implements Serializable {

    private static final long serialVersionUID = 70742667439336106L;
    /**
     * id 主键
     */
    private String id;

    /**
     * name 店铺名称
     */
    private String name;
    /**
     * 模板id
     */
    private String storeTemplateId;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * store_qr_code 店铺二维码URL
     */
    private String storeQrCode;

    /**
     * 店铺状态    关闭 close   正常开放 open
     */
    private String state;

    /**
     * 会员等级
     */
    private String memberLevel;

    /**
     * 认证信息
     */
    private String certificationApproveStatus;

    /**
     * 认证考察方式 1.实地考察2.免考察
     */
    private Integer inspectWay;

    /**
     * '企业类别 (1:房产企业，2：建筑工程，3：政府采购，4：机电安装，5：装饰装修，6：部品企业，7：钢构企业)'
     */
    private String enterpriseCategory;

    /**
     * 企业等级
     */
    private String enterpriseLevel;

    /**
     * 所在地区
     */
    private String enterpriseAddress;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreateTime;
    /**
     * 删除标识 1 已删除 0 未删除
     */
    private int deleted;

}
