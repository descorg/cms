package org.springcms.demo.config;

import org.springcms.demo.service.AdminService;
import org.springcms.demo.service.CityService;
import org.springcms.demo.service.impl.AdminServiceImpl;
import org.springcms.demo.service.impl.CityServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBeanConfig {
    @Bean
    public AdminService adminService() {
        return new AdminServiceImpl();
    }

    @Bean
    public CityService cityService() {
        return new CityServiceImpl();
    }
}
