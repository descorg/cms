package %s.config;

import %s.service.%sService;
import %s.service.impl.%sServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class %sConfig {

    @Bean
    public %sService %sService() {
        return new %sServiceImpl();
    }
}
