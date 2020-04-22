package com.vd.canary.data.api.request.es;

import java.util.List;

/**
 * @Author WangRuilin
 * @Date 2020/4/22 13:31
 */
public class SpecCommand {

    private List<String> value;
    private String name;
    /**针对规格进行特殊处理 长宽高，other进行拆分处理*/
    private String other;

    private Double longMin;
    private Double longMax;

    private Double wideMin;
    private Double wideMax;

    private Double thickMin;
    private Double thickMax;
}
