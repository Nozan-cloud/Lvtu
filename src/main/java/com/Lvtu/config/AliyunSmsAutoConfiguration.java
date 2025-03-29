package com.Lvtu.config;

import com.Lvtu.handler.AliyunSmsHandler;
import com.Lvtu.properties.AliyunSmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliyunSmsAutoConfiguration {
    /**
     * 初始化构建AliyunSmsHandler阿里云短信发送处理器对象
     */
    @Bean
    @ConditionalOnMissingBean
    public AliyunSmsHandler aliyunSendHandler(AliyunSmsProperties properties) {
        AliyunSmsHandler aliyunSmsHandler = new AliyunSmsHandler(properties);
        return aliyunSmsHandler;
    }
}
