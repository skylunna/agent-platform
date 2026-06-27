package com.tiny.agentplatform.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AgentManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentManageApplication.class, args);
    }

}
