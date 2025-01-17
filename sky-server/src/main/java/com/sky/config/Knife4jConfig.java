package com.sky.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @description: Knife4j配置类
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 指定OpenAPI版本
                .openapi("3.0.0")
                .info(new Info()
                        .title("苍穹外卖项目接口文档")
                        .description("苍穹外卖项目接口文档")
                        .version("3.0")
                        .contact(new Contact()
                                .name("Zank")
                                .email("3298232021@qq.com")));
    }
}
