package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: Tan
 * @createTime: 2024/08/23 15:22
 * @description: 配置类：用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfigration {

    @Bean
    @ConditionalOnMissingBean // 保证ioc容器中只有一个Util对象 当没有这个bean的时候再去创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties){
        log.info("开始上传阿里云文件上传工具类对象：{}", aliOssProperties);
        AliOssUtil aliOssUtil = new AliOssUtil(aliOssProperties.getEndpoint()
                                              ,aliOssProperties.getAccessKeyId()
                                              ,aliOssProperties.getAccessKeySecret()
                                              ,aliOssProperties.getBucketName());

        return  aliOssUtil;
    }


}
