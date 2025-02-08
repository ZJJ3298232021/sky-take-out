package com.sky.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sky.json.LocalDateTimeTypeAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Gson 配置类 解决LocalDateTime序列化问题
 */
@Configuration
public class GsonConfig {
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
    }
}
