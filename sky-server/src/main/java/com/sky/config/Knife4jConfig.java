package com.sky.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title("Sky-take-out接口文档")
                        // 接口文档简介
                        .description("苍穹外卖项目接口文档")
                        // 接口文档版本
                        .version("2.0")
                        // 开发者联系方式
                        .contact(new Contact().name("Zank")
                                .email("3298232021@qq.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("ex")
                        .url("http://127.0.0.1:8888"));
    }
}
