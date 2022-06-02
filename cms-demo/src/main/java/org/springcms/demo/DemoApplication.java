package org.springcms.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springcms.core.launch.CmsApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@RefreshScope
@MapperScan(basePackages = {"org.springcms.demo.mapper"})
@SpringBootApplication(scanBasePackages = {"org.springcms.core", "org.springcms.demo"})
public class DemoApplication {
    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);
        CmsApplication.run("cms-demo", DemoApplication.class, args);
    }
}