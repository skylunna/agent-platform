package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.config;

import com.github.xiaoymin.knife4j.spring.configuration.Knife4jProperties;
import com.github.xiaoymin.knife4j.spring.extension.Knife4jOpenApiCustomizer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/5/12 16:31
 * @Description: Knife4jConfig  Knife4j (Swagger) 配置
 */
@Configuration
public class Knife4jConfig {

    private static final String SECURITY_SCHEME_NAME = "Authorization";


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Agent Platform - 管理端 API")
                        .version("1.0.0")
                        .description("Agent 管理平台接口文档，包含 Agent 管理、知识库、对话会话等功能")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@agentplatform.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("请输入登录返回的 Token（无需加 Bearer 前缀）")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8090")
                                .description("本地开发环境")
                ));
    }
}