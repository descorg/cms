package org.springcms.gateway;

import org.springcms.core.launch.CmsApplication;
import org.springcms.gateway.properties.AuthProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
@EnableScheduling
@RefreshScope
@EnableConfigurationProperties(value = AuthProperties.class)
@SpringBootApplication(scanBasePackages = {"org.springcms.core", "org.springcms.gateway"})
public class GatewayApplication {
    public static void main(String[] args) {
//        SpringApplication.run(GatewayApplication.class, args);
        CmsApplication.run("cms-gateway", GatewayApplication.class, args);
    }
}