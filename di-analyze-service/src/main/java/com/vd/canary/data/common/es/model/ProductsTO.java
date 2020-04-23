package com.vd.canary.data.common.es.model;

import lombok.*;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
/**
 * 商品实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductsTO implements Serializable {

    private String skuId;
    //品牌id
    private String proSkuBrandId;
    //spuid
    private String proSkuSpuId;
    //spu编码
    private String proSkuSpuCode;
    //spu名称
    private String proSkuSpuName;
    //sku编码
    private String proSkuSkuCode;
    //sku名称
    private String proSkuSkuName;
    //sku标题
    private String proSkuTitle;
    //sku副标题
    private String proSkuSubTitle;
    //后台三级分类id
    private String threeCategoryId;  // 该值没有实际意义，目前没有赋值
    //后台三级分类code
    private String threeCategoryCode;  // 数组格式
    //后台三级分类
    private String threeCategoryName;  // 数组格式
    //供应商id
    private String skuSupplierId;
    //供应商名称
    private String skuSupplierName;
    //sku状态
    private String skuState;
    //sku图片地址
    private String proSkuSkuPicJson;
    //计价单位
    private String skuValuationUnit;
    //sku介绍
    private String  skuIntroduce;
    //辅助单位
    private String skuAuxiliaryUnit;

    /**
     * 属性attribute_aggregation
     */
    //sku创建时间
    private LocalDateTime skuGmtCreateTime;
    //sku修改时间
    private LocalDateTime skuGmtModifyTime;


    /**
     * product_spu表  商品spu
     */
    //spu状态
    private Integer spuState;
    //spu图片地址
    private String proSpuSpuPic;
    //spu广告词/副标题
    private String spuTitle;


    /**
     * attribute_management表   属性管理
     */
    //属性编码
    private String attributeCode;  // 该字段目前没实际作用  因为商品和属性是一对多的关系，都整合到 attributeMap 字段中保存
    //属性名称
    private String attributeName;  // 该字段目前没实际作用  因为商品和属性是一对多的关系，都整合到 attributeMap 字段中保存


    /**
     * attribute_value表   属性值
     */
    //属性值名称
    private String value_Name;  // 该字段目前没实际作用  因为商品和属性是一对多的关系，都整合到 attributeMap 字段中保存


    /**
     * sku_attribute_relations表   sku属性映射关系
     */
    //属性id -> attribute_management.id
    private String attributeId;  // 该字段目前没实际作用  因为商品和属性是一对多的关系，都整合到 attributeMap 字段中保存
    //属性值id -> attribute_value.id
    private String attributeValueId;  // 该字段目前没实际作用  因为商品和属性是一对多的关系，都整合到 attributeMap 字段中保存
    // 商品 所有属性和属性值 合集 格式如下：
    /**
     * [{
     *    "attributeType": "1",
     *    "attributeId": "1252129898609266689",
     *    "attributeName": "颜色",
     *    "attributeValue": [{"attributeValueId":"1252129898630238210","attributeValueName":"红"},{"attributeValueId":"1252129898651209730","attributeValueName":"蓝"}]
     * }]
     *
     */
    private String attributeMap;
    //属性类型：0定价属性   1一般属性
    private Integer attributeType;  //  该字段目前没实际作用  因为商品和属性是一对多的关系，都整合到 attributeMap 字段中保存


    /**
     * background_category表  后台类目
     */
    //后台一级分类id
    private String oneCategoryId;  // 该值没有实际意义，目前没有赋值
    //后台一级分类code
    private String oneCategoryCode;  // 数组格式
    //后台一级分类
    private String oneCategoryName;  // 数组格式
    //后台二级分类id
    private String twoCategoryId;  // 该值没有实际意义，目前没有赋值
    //后台二级分类code
    private String twoCategoryCode;  // 数组格式
    //后台二级分类
    private String twoCategoryName;  // 数组格式

    /**
     * brand_management表   品牌管理
     */
    //品牌编码
    private String brandCode;
    //品牌名称
    private String bBrandName;
    //品牌Logo
    private String brandLoge;
    //品牌简写
    private String brandShorthand;
    //品牌介绍
    private String brandIntroduction;


    /**
     * category_attribute_relations表   类目属性映射关系
     */


    /**
     * category_relations前后台类目映射关系
     */
    //前台一级分类id
    private String fOneCategoryId;  // 该值没有实际意义，目前没有赋值
    //前台一级分类code
    private String fOneCategoryCode; // 数组格式
    //前台一级分类
    private String fOneCategoryName; // 数组格式
    //前台二级分类id
    private String fTwoCategoryId;  // 该值没有实际意义，目前没有赋值
    //前台二级分类code
    private String fTwoCategoryCode;  // 数组格式
    //前台二级分类
    private String fTwoCategoryName;  // 数组格式
    //前台三级分类id
    private String fThreeCategoryId;  // 该值没有实际意义，目前没有赋值
    //前台三级分类code
    private String fThreeCategoryCode;  // 数组格式
    //前台三级分类
    private String fThreeCategoryName;  // 数组格式

    /**
     * file_management表   文件管理
     */
    //文件类型  0图片 1视频
    private String type; // 该字段目前没实际作用  因为商品和图片属性是一对多的关系，都整合到 proSkuSkuPicJson 字段中保存
    //文件存储地址
    private String fileUrl;// 该字段目前没实际作用  因为商品和图片属性是一对多的关系，都整合到 proSkuSkuPicJson 字段中保存
    //排序号
    private Integer fileSortNumber;// 该字段目前没实际作用  因为商品和图片属性是一对多的关系，都整合到 proSkuSkuPicJson 字段中保存


    /**
     * regional_management表   区域管理
     */
    //区域编码
    private String regionalCode;  // 写搜索缺失
    //区域名称
    private String regionalName;// 写搜索缺失
    //区域范围json
    //private Text regionalScope;
    private String regionalScope;// 写搜索缺失

    /**
     * sku_selling_price表   sku定价管理
     */
    //商品定价信息
    private String skuSellPriceJson;
    //定价类型
    private Integer skuSellPriceType;


    /**
     * sku_warehouse_relations表   sku仓库关联表
     */
    //仓库id
    private String warehouseId;
    //仓库名称
    private String warehouseName;
    //库存
    private String inventory;
    //供货区域id
    private String regionalId;
    //供货区域名称
    private String skuRegionalName;


    /**
     * store_product_relations   店铺商品
     */
    //店铺id
    private String storeId;
    //店铺分类id
    private String categoryId;
    // 店铺名称
    private String storeName;

    // 店铺相关的信息，从店铺索引拿
    //主营类目
    private String businessCategory;
    //主营产品
    private String mainProducts;
    //所在地区
    private String businessArea;
    //展厅编号
    private String boothBusinessBoothCode; //List boothBusinessBoothCode;
    //会员等级
    private String customerProfilesLevel;
    //认证信息
    private String approveState;
    //供方类别
    private String enterpriseType;
    //店铺二维码
    private String storeInfoStoreQrCode;
    //创建时间
    private Date gmtCreateTime;
    private Date boothScheduledTime; //入驻时间
    /**
     * warehouse_management表    仓库管理
     */
    //仓库编码
    private String warehouseCode; // 写搜索缺失
    //仓库类型
    private String warehouseType;// 写搜索缺失
    //省市区json
    private String warehouseRegional;// 写搜索缺失
    //详细地址
    private String detailedAddress;// 写搜索缺失


    /**
     * [{
     *    "attributeType": "1",
     *    "attributeId": "1252129898609266689",
     *    "attributeName": "颜色",
     *    "attributeValue": [{"attributeValueId":"1252129898630238210","attributeValueName":"红"},{"attributeValueId":"1252129898651209730","attributeValueName":"蓝"}]
     * }]
     *
     */
    private String spuAttributeMap;   ///暂时还没加

}
