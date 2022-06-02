package org.springcms;

import org.springcms.core.launch.CmsApplication;
import org.springcms.properties.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
@EnableScheduling
@SpringBootApplication
@RefreshScope
@EnableConfigurationProperties(value = AuthProperties.class)
public class GatewayApplication {
    public static void main(String[] args) {
//        SpringApplication.run(GatewayApplication.class, args);
        CmsApplication.run("cms-gateway", GatewayApplication.class, args);
    }
}