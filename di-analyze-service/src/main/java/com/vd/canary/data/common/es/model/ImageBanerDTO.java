package com.vd.canary.data.common.es.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: di-analyze->imageBanerDto
 * @description:
 * @author: zcy
 * @create: 2020-04-22 14:59
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageBanerDTO implements Serializable {
    private static final long serialVersionUID = -8384317417496292082L;
    private String imageOrder;
    private String imageName;
    private  String imageUrl;

}
