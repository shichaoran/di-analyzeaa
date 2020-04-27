package com.vd.canary.data.api.response.es.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@ToString
public class CategoryVO implements Serializable {

    //前台一级分类code
    private String oneCategoryCode;
    //前台一级分类
    private String oneCategoryName;
    //前台二级分类code
    private String twoCategoryCode;
    //前台二级分类
    private String twoCategoryName;
    //前台三级分类code
    private String threeCategoryCode;
    //前台三级分类
    private String threeCategoryName;


}
