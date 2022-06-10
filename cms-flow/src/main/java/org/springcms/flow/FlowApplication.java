package org.springcms.flow;

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
@SpringBootApplication
public class FlowApplication {
    public static void main(String[] args) {
        CmsApplication.run("cms-flow", FlowApplication.class, args);
    }
}
