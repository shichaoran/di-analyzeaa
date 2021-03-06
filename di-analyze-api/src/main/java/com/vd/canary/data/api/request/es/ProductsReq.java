package com.vd.canary.data.api.request.es;

import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Getter
@Setter
public class ProductsReq extends ProductsBaseReq {
    /**
     * 输入框中关键字
     */
    private String key;

}
