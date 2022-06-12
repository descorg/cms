package org.springcms.rabbit;

import org.mybatis.spring.annotation.MapperScan;
import org.springcms.core.launch.CmsApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableDiscoveryClient
@EnableFeignClients
@RefreshScope
@EnableOpenApi
@MapperScan(basePackages = {"org.springcms.rabbit.core.mapper"})
@SpringBootApplication
public class RabbitApplication {
    public static void main(String[] args) {
        CmsApplication.run("cms-rabbit", RabbitApplication.class, args);
    }
}