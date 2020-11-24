package com.jiebao.platfrom.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2 支持
 * @Author Sinliz
 * @Date 2020/3/16 10:26
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jiebao.platfrom"))
                .paths(PathSelectors.any())
                .build().apiInfo(new ApiInfoBuilder()
                        .title("jiebao-pwp-spring 接口文档")
                        .description("接口文档")
                        .version("v1.0")
                        .contact(new Contact("Sinliz",null,null))
                        .license("The Apache License")
                        .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                        .build());
    }
}
