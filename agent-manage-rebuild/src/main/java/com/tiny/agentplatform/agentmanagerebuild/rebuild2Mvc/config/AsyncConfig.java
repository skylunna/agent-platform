package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:54
 * @description: AsyncConfig
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "auditExecutor")
    public Executor auditExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(2);
        exec.setMaxPoolSize(4);
        exec.setQueueCapacity(100);
        exec.setThreadNamePrefix("audit-");
        exec.initialize();
        return exec;
    }
}