package com.vd.canary.data.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.config.UrlsConfig;
import com.vd.canary.obmp.customer.api.request.customer.store.StoreDataQueryReq;
import com.vd.canary.obmp.customer.api.response.customer.StoreDataInfoResp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @program: HttpClientUtils
 * @author: liuxh
 * @create: 2020-04-06 09:19
 **/
@Component
public class HttpClientUtils {
    @Autowired
    private UrlsConfig urlsConfig;

    RestTemplate directRestTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());


    public HttpClientUtils() {
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method,
            ParameterizedTypeReference<T> responseType) {
        String requestUrl=urlsConfig.getIpPort()+url;
        RestTemplate template = this.getRestTemplate();
        return template.exchange(requestUrl, method, HttpEntity.EMPTY, responseType, new Object[0]);
    }

    public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, ParameterizedTypeReference<T> responseType) {
        String requestUrl=urlsConfig.getIpPort()+url;
        RestTemplate template = this.getRestTemplate();
        return template.exchange(requestUrl, method, requestEntity, responseType, new Object[0]);
    }

    private RestTemplate getRestTemplate() {
        return this.directRestTemplate;
    }

    /**
     * 采用POST请求，数据格式为 application/json，并且返回结果是JSON string
     * @param url
     * @param
     * @return
     */
    public   String postForJson(String url, String json) {
       // String requestUrl=urlsConfig.getIpPort1()+url;
        String requestUrl="http://172.16.32.247:9035"+url;

        //设置Http Header
        HttpHeaders headers = new HttpHeaders();
        //设置请求媒体数据类型
        headers.setContentType(MediaType.APPLICATION_JSON);
        //设置返回媒体数据类型
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add("x-client-id","B2B_Operation");
        HttpEntity<String> formEntity = new HttpEntity<String>(json, headers);
        return directRestTemplate.postForObject(requestUrl, formEntity, String.class);
    }




    /**
     * 采用POST请求，数据格式为 application/json，并且返回结果是JSON string
     * @param url
     * @param
     * @return
     */
    public  String postForFormData(String url, String json) {

        String requestUrl=urlsConfig.getIpPort()+url;
        //设置Http Header
        HttpHeaders headers = new HttpHeaders();
        //设置请求媒体数据类型
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //设置返回媒体数据类型
       headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Map<String,Object> jsonMap = JSON.parseObject(json, Map.class);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        for (String key : jsonMap.keySet()) {
            map.add(key,jsonMap.get(key)+"");
        }
        map.add("bookId","1000");
        map.add("groupId","1000");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = directRestTemplate.postForEntity( requestUrl, request , String.class );
        return response.getBody();
    }

    /**
     * 采用POST请求，数据格式为 application/json，并且返回结果是JSON string
     * @param url
     * @param
     * @return
     */
    public  String postForFormDataList(String url, String json) {
        String requestUrl=urlsConfig.getIpPort()+url;
        //设置Http Header
        HttpHeaders headers = new HttpHeaders();
        //设置请求媒体数据类型
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //设置返回媒体数据类型
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        Map<String,Object> jsonMap = JSON.parseObject(json, Map.class);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        for (String key : jsonMap.keySet()) {
            map.add(key,jsonMap.get(key)+"");
        }
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = directRestTemplate.postForEntity( requestUrl, request , String.class );
        return response.getBody();
    }

    public StoreDataInfoResp getStoreDataInfoResp(StoreDataQueryReq storeDataQueryReq) {
        String json = this.postForJson("/data/queryStoreDataInfo", JSON.toJSONString(storeDataQueryReq));
        JSONObject jsonObject = JSON.parseObject(json);
        String jsonObjectDtat = jsonObject.getString("data");
        return JSON.parseObject(jsonObjectDtat, StoreDataInfoResp.class);
    }
}
