package com.tiny.agentplatform.manage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 18:43
 * @description: PythonClientConfig
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "cdss.python")
public class PythonClientConfig {


    private String baseUrl;
    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(60);
    private Duration streamReadTimeout = Duration.ofMinutes(5);


    @Bean
    public RestClient pythonRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) connectTimeout.toMillis());
        factory.setReadTimeout((int) readTimeout.toMillis());

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    @Bean
    public WebClient pythonWebClient() {
        // WebClient 用于 SSE 流式调用 (Step 6 用)
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
