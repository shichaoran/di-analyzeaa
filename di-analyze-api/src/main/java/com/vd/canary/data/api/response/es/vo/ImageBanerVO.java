package com.vd.canary.data.api.response.es.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: di-analyze->ImageBanerVO
 * @description:
 * @author: zcy
 * @create: 2020-04-22 15:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageBanerVO implements Serializable {

    private String imageOrder;
    private String imageName;
    private  String imageUrl;

}
