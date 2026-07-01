package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:45
 * @description: PythonChatRequest
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PythonChatRequest {

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("kb_id")
    private String kbId;

    private List<Message> messages;

    private Boolean stream;

    @JsonProperty("agent_config")
    private AgentConfig agentConfig;

    @Data
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AgentConfig {
        @JsonProperty("agent_id")
        private String agentId;

        @JsonProperty("system_prompt")
        private String systemPrompt;

        private String model;
        private BigDecimal temperature;

        @JsonProperty("max_iterations")
        private Integer maxIterations;

        @JsonProperty("tools_enabled")
        private List<String> toolsEnabled;
    }
}
