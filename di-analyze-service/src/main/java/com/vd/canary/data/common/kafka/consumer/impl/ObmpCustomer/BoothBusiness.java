package com.vd.canary.data.common.kafka.consumer.impl.ObmpCustomer;

import com.alibaba.fastjson.JSON;
import com.vd.canary.data.api.response.es.vo.ShopTo;
import com.vd.canary.data.common.kafka.consumer.impl.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author shichaoran
 * @Date 2020/4/9 10:53
 * @Version
 */
public class BoothBusiness implements Function {
    private static final Logger logger = LoggerFactory.getLogger(BoothBusiness.class);
/**
 * 通过店铺->coustemer->展位编号
 */

    @Override
    public void performES(String msg) {
        logger.info("BoothBusinessBoothCode.msg"+msg);

        HashMap hashMap = JSON.parseObject(msg, HashMap.class);
        Set<Map.Entry<String, String>> entries = hashMap.entrySet();
        ShopTo shopTo = new ShopTo();
        for (Map.Entry<String, String> entry : entries) {
            logger.info("key={},value={}" + entry.getKey(), entry.getValue());
            if (entry.getKey().equals("")
            ){
                shopTo.setBoothCode(entry.getKey());
                shopTo.setCustomerId(entry.getKey());
            }

        }
    }
}
