package com.tiny.agentplatform.manage.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:47
 * @description: ChatEventCollector
 *
 *  收集 Python 发回的 SSE 事件，用于:
 *      1. 拼接完整 answer
 *      2. 累积完整 trace (审计用)
 *      3. 提取 citations
 *      4. 抓取 token usage / iterations
 */
@Slf4j
@Getter
public class ChatEventCollector {

    private final ObjectMapper objectMapper;
    private final StringBuilder answer = new StringBuilder();
    private final ArrayNode trace;
    private final ArrayNode citations;

    private Integer iterations;
    private Integer promptTokens;
    private Integer completionTokens;
    private Integer totalTokens;
    private String errorMessage;
    private boolean hasError = false;
    private boolean isDone = false;

    public ChatEventCollector(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.trace = objectMapper.createArrayNode();
        this.citations = objectMapper.createArrayNode();
    }

    /**
     * 处理一个 SSE 事件
     * @param event 事件名 (token / tool_call / tool_result / done / error)
     * @param data  事件数据 (已解析为 JsonNode)
     */
    public void accept(String event, JsonNode data) {
        // 1. 记录到 trace
        ArrayNode entry = objectMapper.createArrayNode();
        entry.add(event);
        entry.add(data);
        trace.add(entry);

        // 2. 按事件类型处理
        switch (event) {
            case "token" -> {
                String content = data.path("content").asText("");
                answer.append(content);
            }
            case "tool_result" -> extractCitationsIfAny(data);
            case "done" -> {
                iterations = data.path("iterations").asInt(0);
                promptTokens = data.path("prompt_tokens").asInt(0);
                completionTokens = data.path("completion_tokens").asInt(0);
                totalTokens = data.path("total_tokens").asInt(0);
                isDone = true;
            }
            case "error" -> {
                errorMessage = data.path("message").asText("Unknown error");
                hasError = true;
            }
            default -> {
                // tool_call 等其他事件已经记录到 trace,无需特殊处理
            }
        }
    }

    private void extractCitationsIfAny(JsonNode data) {
        String toolName = data.path("name").asText();
        if (!"search_knowledge_base".equals(toolName)) {
            return;
        }
        // tool_result.content 是 JSON 字符串
        String contentStr = data.path("content").asText();
        try {
            JsonNode content = objectMapper.readTree(contentStr);
            if (!"ok".equals(content.path("status").asText())) {
                return;
            }
            JsonNode chunks = content.path("data").path("chunks");
            if (chunks.isArray()) {
                chunks.forEach(citations::add);
            }
        } catch (Exception e) {
            log.warn("Failed to parse tool_result content: {}", e.getMessage());
        }
    }

    public String getAnswer() {
        return answer.toString();
    }

    public String getStatus() {
        if (hasError) return "failed";
        if (isDone) return "success";
        return "incomplete";
    }
}
