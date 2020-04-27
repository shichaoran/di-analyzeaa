package com.vd.canary.data.repository.es.model;

import lombok.Data;

/**
 * @Author shichaoran
 * @Date 2020/4/24 9:55
 * @Version
 */
@Data
public class Goddess {
    private Long id;
    private String bread;  //品名
    private String spec;   //规格
    private String material;  //材质
    private String place;   //钢厂/产地
    private String price;//价格
    private String raise;//涨跌
    private String note;//备注


}
