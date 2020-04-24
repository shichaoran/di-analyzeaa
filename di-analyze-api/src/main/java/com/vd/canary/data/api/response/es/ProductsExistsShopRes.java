package com.vd.canary.data.api.response.es;

//import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.vd.canary.data.api.response.es.vo.ProductsDetailRes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductsExistsShopRes implements Serializable {

    private String storeId;

    private String storeName;

    private String logoImageUrl;


}
