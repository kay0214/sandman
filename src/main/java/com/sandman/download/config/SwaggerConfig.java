/*
package com.sandman.download.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

*/
/**
 * Created by wangj on 2018/4/20.
 *//*

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket ProductApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(productApiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.sandman.download.web.rest"))
            .paths(PathSelectors.any())
            .build();
    }

    private ApiInfo productApiInfo() {
        ApiInfo apiInfo = new ApiInfo("XXX系统数据接口文档",
            "文档描述。。。",
            "1.0.0",
            "API TERMS URL",
            "联系人邮箱",
            "license",
            "license url");
        return apiInfo;
    }
}
*/
