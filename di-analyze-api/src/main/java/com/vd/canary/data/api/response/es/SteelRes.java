package com.vd.canary.data.api.response.es;

import com.vd.canary.data.api.response.es.vo.ProductsDetailRes;
import com.vd.canary.data.api.response.es.vo.SteelVO;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SteelRes implements Serializable {

    private Map<String,String> categorys;//fThreeCategoryId:fThreeCategoryName

    private Map<String,String> brands; //proSkuBrandId:bBrandName

    private Map<String,Map<String,String>> attributes; //属性

    private List<SteelVO> SteelVORes; //商品详细列表

    private Map<String,String> spuNames;

    private Integer total;//总条数

}
