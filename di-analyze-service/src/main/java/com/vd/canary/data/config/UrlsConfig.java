package com.vd.canary.data.config;

import java.io.Serializable;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liuxh
 */
@Data
@Component
@ConfigurationProperties(prefix = "httpurl")
public class UrlsConfig implements Serializable {
    private static final long serialVersionUID = 6698577422078943370L;
    private String ipPort;

    private String ipPort1;
    private String tlerp_arm_api_url;

}
