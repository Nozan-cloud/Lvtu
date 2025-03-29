package com.Lvtu.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lvtu.sms")
@Data
public class AliyunSmsProperties {
    private String endpoint="cn-hangzhou";
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
    private String regionId;
}
