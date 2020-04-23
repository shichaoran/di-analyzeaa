package com.vd.canary.data.api.response.es;

//import com.alibaba.fastjson.JSON;
import lombok.*;
import lombok.experimental.Accessors;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductSpuResponse implements Serializable {

    private String spuId;
    private String spuName;
    private String spuPic;
    private String spuTitle;
    private List<ProductSkuVO> productSkuVO;

}
