package org.springcms.demo.config;

import org.springcms.core.jwt.constant.TokenConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("(?!/error.*).*"))
                .build()
                .globalRequestParameters(header());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Swagger API")
                .description("demo-唐荣")
                .termsOfServiceUrl("")
                .contact(new Contact("descorg", "", "tang_rong7407@163.com"))
                .version("2.0")
                .build();
    }

    private List<RequestParameter> header() {
        return java.util.Collections.singletonList(new springfox.documentation.builders.RequestParameterBuilder()
                // 不能叫Authorization
                .name(TokenConstant.HEADER)
                .description(TokenConstant.HEADER)
                .in(ParameterType.HEADER)
                .required(true)
                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                .build());
    }
}
