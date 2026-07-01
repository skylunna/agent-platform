package com.tiny.agentplatform.agentmanagerebuild;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgentManageRebuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentManageRebuildApplication.class, args);
    }

}
