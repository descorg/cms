package org.springcms.report;

import com.bstek.ureport.console.UReportServlet;
import org.springcms.core.launch.CmsApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@EnableDiscoveryClient
@EnableFeignClients
@RefreshScope
@ImportResource("classpath:context.xml")
@SpringBootApplication
public class ReportApplication {
    public static void main(String[] args) {
        CmsApplication.run("cms-report", ReportApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean buildUReportServlet(){
        return new ServletRegistrationBean(new UReportServlet(),"/ureport/*");
    }
}