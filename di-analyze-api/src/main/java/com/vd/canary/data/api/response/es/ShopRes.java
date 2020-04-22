package com.vd.canary.data.api.response.es;

import java.io.Serializable;
import java.util.List;

import com.vd.canary.data.api.response.es.vo.ShopVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @Author shichaoran
 * @Date 2020/4/14 16:03
 * @Version
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopRes implements Serializable {
    private static final long serialVersionUID = 3799924045437177866L;
    private List<ShopVo> shopVos;
    /**
     * 品牌类目集合
     */
    private List<String> brands;
    /**
     * 类目集合
     */
    private List<String> categories;

    private int total;
}
