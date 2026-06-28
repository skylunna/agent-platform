package com.tiny.agentplatform.manage.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiny.agentplatform.manage.config.PythonClientConfig;
import com.tiny.agentplatform.manage.dto.python.PythonChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;


/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:48
 * @description: PythonStreamClient
 *
 *  Python 流式 chat 客户端
 *  返回 Flux<SseFrame>, 每个 frame 是一个 (event, data) 对
 */
@Slf4j
@Component
public class PythonStreamClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final Duration streamReadTimeout;

    public PythonStreamClient(WebClient pythonWebClient,
                              ObjectMapper objectMapper,
                              PythonClientConfig config) {
        this.webClient = pythonWebClient;
        this.objectMapper = objectMapper;
        this.streamReadTimeout = config.getStreamReadTimeout();
    }

    public Flux<SseFrame> chatStream(PythonChatRequest request) {
        ParameterizedTypeReference<ServerSentEvent<String>> type =
                new ParameterizedTypeReference<>() {};

        return webClient.post()
                .uri("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(type)
                .timeout(streamReadTimeout)
                .mapNotNull(this::toFrame)
                .doOnError(e -> log.error("Python stream error: {}", e.getMessage()));
    }

    private SseFrame toFrame(ServerSentEvent<String> sse) {
        String event = sse.event();
        String dataStr = sse.data();
        if (event == null || dataStr == null) {
            return null;
        }
        try {
            JsonNode data = objectMapper.readTree(dataStr);
            return new SseFrame(event, data, dataStr);
        } catch (Exception e) {
            log.warn("Failed to parse SSE data: {}", dataStr);
            return null;
        }
    }

    /**
     * 单个 SSE 帧
     * @param event   事件类型
     * @param data    解析后的 JsonNode (供 collector 用)
     * @param rawData 原始 JSON 字符串 (供转发给前端用,避免再次序列化)
     */
    public record SseFrame(String event, JsonNode data, String rawData) {}
}
