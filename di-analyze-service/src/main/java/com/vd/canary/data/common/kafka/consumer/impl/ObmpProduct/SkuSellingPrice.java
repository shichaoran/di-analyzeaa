package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.api.response.es.ShopProductRes;
import com.vd.canary.data.common.es.model.ProductsTO;
import com.vd.canary.data.common.es.service.impl.ProductESServiceImpl;
import com.vd.canary.data.common.es.service.impl.ShopESServiceImpl;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import com.vd.canary.data.util.StringUtil;
import com.vd.canary.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SkuSellingPrice implements Function {

    @Autowired
    private ProductESServiceImpl productESServiceImplTemp;

    @Autowired
    public ShopESServiceImpl shopESService;

    @Override
    public void performES(String msg) {
        log.info("SkuSellingPrice.msg" + msg);

        if(StringUtils.isEmpty(msg)){
            return;
        }
        HashMap hashMap = JSON.parseObject(msg, HashMap.class);
        String type = (String) hashMap.get("type");
        String skuid = null;
        HashMap<String,Object> binlogMap = null;
        if(hashMap.containsKey("info")){
            binlogMap = JSON.parseObject(hashMap.get("info").toString(), HashMap.class);
        }
        ProductsTO productsTO = null;
        if (type.equals("insert") || type.equals("update")) {
            if(binlogMap != null && binlogMap.size() > 0){
                skuid = binlogMap.get("sku_id").toString();
                try {
                    Map<String, Object> esMap = productESServiceImplTemp.findById(skuid);
                    log.info("SkuSellingPrice.performES,brand_id.esMap={}.", JSONUtil.toJSON(esMap).toJSONString());
                    if(esMap != null){
                        Map<String, Object> resjson = reSetValue(esMap, binlogMap);
                        productESServiceImplTemp.updateProduct(resjson);
                    }
                    // 商品新增后同步店铺索引中的字段：businessCategory businessBrand skuPrice ...
                    updateShopIndexProduct(esMap);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public Map<String, Object> reSetValue(Map<String, Object> esMap,Map<String,Object> binlogMap){
        if(binlogMap.containsKey("price_type")) esMap.put("skuSellPriceType",binlogMap.get("price_type"));
        if(binlogMap.containsKey("price_json")) esMap.put("skuSellPriceJson",binlogMap.get("price_json"));
        System.out.println("------------SkuSellingPrice.reSetValue.json:"+esMap);
        return esMap;
    }

    public void updateShopIndexProduct(Map<String, Object> esProductMap){
        if(esProductMap == null || esProductMap.get("storeId") == null){
            return;
        }
        String storeId = esProductMap.get("storeId").toString();
        if(StringUtils.isEmpty(storeId)){
            return;
        }
        Map queryProduct = new HashMap<>();
        queryProduct.put("storeId",storeId);
        List<Map<String, Object>> products = productESServiceImplTemp.boolQueryByCustomMap(queryProduct);
        List<String> businessCategory = new ArrayList<>();
        List<String> businessBrand = new ArrayList<>();
        List<ShopProductRes> shopProductRes = new ArrayList<>();
        for(Map<String, Object> productMap : products){

            if(productMap.containsKey("fThreeCategoryName") && productMap.get("fThreeCategoryName") != null) {
                String temp = JSONUtil.toJSONString(productMap.get("fThreeCategoryName"));
                if(StringUtils.isNotBlank(temp)){
                    List<String> businessCategoryList = JSONArray.parseArray(temp,String.class);
                    businessCategory.addAll(businessCategoryList);
                }
            }

            if(productMap.containsKey("bBrandName") && productMap.get("bBrandName") != null){
                businessBrand.add(productMap.get("bBrandName").toString());
            }

            ShopProductRes shopProduct = new ShopProductRes();

            if(productMap.containsKey("skuId") && productMap.get("skuId") != null) shopProduct.setSkuId(productMap.get("skuId").toString());

            if(productMap.containsKey("proSkuSkuName") && productMap.get("proSkuSkuName") != null) shopProduct.setSkuName(productMap.get("proSkuSkuName").toString());

            if(productMap.containsKey("proSkuSkuPicJson") && productMap.get("proSkuSkuPicJson") != null) shopProduct.setSkuPic(productMap.get("proSkuSkuPicJson").toString());

            if(productMap.containsKey("skuSellPriceJson") && productMap.get("skuSellPriceJson") != null) shopProduct.setSkuPrice(productMap.get("skuSellPriceJson").toString());

            if(productMap.containsKey("proSkuTitle") && productMap.get("proSkuTitle") != null) shopProduct.setSkuTitle(productMap.get("proSkuTitle").toString());

            if(productMap.containsKey("proSkuSubTitle") && productMap.get("proSkuSubTitle") != null) shopProduct.setSkuSubtitle(productMap.get("proSkuSubTitle").toString());

            if(productMap.containsKey("skuValuationUnit") && productMap.get("skuValuationUnit") != null) shopProduct.setUnit(productMap.get("skuValuationUnit").toString());

            if(productMap.containsKey("skuSellPriceType") && productMap.get("skuSellPriceType") != null) shopProduct.setPriceType(productMap.get("skuSellPriceType").toString());

            if(productMap.containsKey("skuGmtCreateTime") && productMap.get("skuGmtCreateTime") != null){
                LocalDateTime t = LocalDateTime.parse(productMap.get("skuGmtCreateTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                shopProduct.setCrateDate(t);
            }

            if(productMap.containsKey("proSkuSpuId") && productMap.get("proSkuSpuId") != null) shopProduct.setProSkuSpuId(productMap.get("proSkuSpuId").toString());

            shopProductRes.add(shopProduct);
        }
        // 对商品列表按时间排序
        Collections.sort(shopProductRes, new Comparator<ShopProductRes>(){
            /*
             * int compare(Person p1, Person p2) 返回一个基本类型的整型，
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(ShopProductRes shop1, ShopProductRes shop2) {
                //按照shop1.getCrateDate() 进行倒序排列
                if( Timestamp.valueOf(shop1.getCrateDate()).getTime() < Timestamp.valueOf(shop2.getCrateDate()).getTime() ){
                    return 1;
                }
                if( Timestamp.valueOf(shop1.getCrateDate()).getTime() == Timestamp.valueOf(shop2.getCrateDate()).getTime() ){
                    return 0;
                }
                return -1;
            }
        });

        try {
            Map<String, Object> esShopMap = shopESService.findById(storeId);
            esShopMap.put("businessCategory",businessCategory);
            esShopMap.put("businessBrand",businessBrand);
            if(shopProductRes.size() >=3 ){
                JSONArray array = JSONArray.parseArray(JSONUtil.toJSONString(shopProductRes.subList(0,2)));
                esShopMap.put("shopProductRes",array);
            }else{
                JSONArray array = JSONArray.parseArray(JSONUtil.toJSONString(shopProductRes));
                esShopMap.put("shopProductRes",array);
            }

            shopESService.updateShop(esShopMap);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*public static void main(String[] args) {
        String[] regulation = {"诸葛亮","鲁班","xzcx","貂蝉","吕布"};
        final List<String> regulationOrder = Arrays.asList(regulation);
        String[] ordered = {"nice","貂蝉","诸葛亮","xzcx","吕布","貂蝉","鲁班","诸葛亮","貂蝉","鲁班","诸葛亮","hahahahah","adsad"};
        List<String> orderedList = Arrays.asList(ordered);
        Collections.sort(orderedList, new Comparator<String>()
        {
            public int compare(String o1, String o2)
            {
                int io1 = regulationOrder.indexOf(o1);
                int io2 = regulationOrder.indexOf(o2);
                if(io1 == -1){
                    return 1;
                }
                if(io2 == -1){
                    return -1;
                }
                return io1 - io2;
            }
        });
        System.out.println(orderedList);
    }*/
    public static class Person {
        private String id;
        private String name;
        private int age;

        public Person(String id, String name, int age) {
            super();
            this.id = id;
            this.name = name;
            this.age = age;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
    }
    public static void main(String[] args) {
        List<Person> plist = new ArrayList<Person>();
        //创建3个Person对象，年龄分别是32、20、25，并将他们依次放入List中
        Person p1 = new Person("0001","zhangsan",32);
        Person p2 = new Person("0002","lisi",20);
        Person p3 = new Person("0003","wangwu",25);
        plist.add(p1);
        plist.add(p2);
        plist.add(p3);
        System.out.println("排序前的结果：" + JSONObject.toJSON(plist).toString());
        Collections.sort(plist, new Comparator<Person>(){
            /*
             * int compare(Person p1, Person p2) 返回一个基本类型的整型，
             * 返回负数表示：p1 小于p2，
             * 返回0 表示：p1和p2相等，
             * 返回正数表示：p1大于p2
             */
            public int compare(Person p1, Person p2) {
                //按照Person的年龄进行升序排列
                if(p1.getAge() < p2.getAge()){
                    return 1;
                }
                if(p1.getAge() == p2.getAge()){
                    return 0;
                }
                return -1;
            }
        });
        System.out.println("排序后的结果："+JSONObject.toJSON(plist).toString());
    }


}
