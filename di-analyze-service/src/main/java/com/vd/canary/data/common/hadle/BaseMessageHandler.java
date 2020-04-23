package com.vd.canary.data.common.hadle;

import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Component;

/**
 * @author user
 */
@Component
public class BaseMessageHandler {
    public <T> T getRawData(JSONObject data, String dataBase, String name,
                            Class<T> clazz) {
         if (data == null) {
            return null;
        }
        String schemaName = data.getString("database");
        String tableName =  data.getString("table");
        if (dataBase.equals(schemaName) && name.equals(tableName)) {
            JSONObject info = data.getJSONObject("info");
            if (!info.isEmpty()) {
                T t = JSONObject.parseObject(info.toJSONString(), clazz);
                return t;
            }
        }
        return null;
    }
}
