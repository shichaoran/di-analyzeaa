package com.vd.canary.data.service.es.threeService;

/**
 * @Author shichaoran
 * @Date 2020/4/17 15:03
 * @Version
 */


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import com.vd.canary.data.common.es.clientconfig.HttpClient;
import com.vd.canary.data.repository.es.threedemo.HttpHeader;
import com.vd.canary.data.repository.es.threedemo.HttpParamers;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class HttpService {
    private String serverUrl;
    private int connectTimeout = 15000;
    private int readTimeout = 30000;

    public HttpService(String serverUrl) {
        this.serverUrl = serverUrl.trim();
    }

    public Map<String, Object> commonService(String serviceUrl, HttpParamers paramers) throws Exception {
        return commonService(serviceUrl, paramers, null);
    }

    public Map<String, Object> commonService(String serviceUrl, HttpParamers paramers, HttpHeader header) throws Exception {
        String response = service(serviceUrl, paramers, header);
        try {
            Map<String, Object> result = JSONObject.parseObject(response, new TypeReference<Map<String, Object>>() {
            });
            if ((result == null) || (result.isEmpty())) {
                throw new Exception("远程服务返回的数据无法解析");
            }
            Integer code = (Integer) result.get("code");
            if ((code == null) || (code.intValue() != 0)) {
                throw new Exception((String) result.get("message"));
            }
            return result;
        } catch (Exception e) {
            throw new Exception("返回结果异常,response:" + response, e);
        }
    }

    public String service(String serviceUrl, HttpParamers paramers) throws Exception {
        return service(serviceUrl, paramers, null);
    }

    public String service(String serviceUrl, HttpParamers paramers, HttpHeader header) throws Exception {
        String url = this.serverUrl + serviceUrl;
        String responseData = "";
        try {
            responseData = HttpClient.doService(url, paramers, header, this.connectTimeout, this.readTimeout);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
        return responseData;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}