package com.vd.canary.data.repository.es.threedemo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.service.es.threeService.HttpService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author shichaoran
 * @Date 2020/4/19 0:05
 * @Version
 */
public class RemoteDataUtil {
    public static String getData() throws Exception {
        // 1根据token获取url
        String response = "";
        List<String> list = new ArrayList<>();
        String uri = "http://open.api.mysteel.cn/zjsj/api_marketl_zjsjl.html?page=1&tableId=15608&pageSize=200&token=IgtdMORZvrU7BZ4PGBBPLK34U4cNuB5F&startTime=2020-01-01&endTime=2020-04-30";
        HttpParamers paramers = HttpParamers.httpPostParamers();
        paramers.addParam("time", String.valueOf(System.currentTimeMillis()));
        HttpService httpService = new HttpService(uri);
        response = httpService.service("/posts", paramers);
        JSONObject jsonObject = JSONObject.parseObject(response);
        Object markets = jsonObject.get("markets");
        if (markets != null) {
            JSONArray jsonArray = JSON.parseArray(markets.toString());
            jsonArray.forEach(json -> {
                JSONObject jsonObject1 = JSONObject.parseObject(json.toString());
                Object urlObject = jsonObject1.get("url");
                AtomicReference<String> response1 = new AtomicReference<>("");


                HttpService httpService1 = new HttpService(urlObject.toString());
                try {
                    response1.set(httpService1.service(urlObject.toString(), paramers));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                list.add(response1.get());
            });

        }
            return list.toString();


    }
}
