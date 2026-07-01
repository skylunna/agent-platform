package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 18:50
 * @description: PythonExecutionClient
 *
 *  Python 执行面 HTTP 客户端
 *  同步调用
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PythonExecutionClient {

    private final RestClient pythonRestClient;

    /**
     * 创建知识库 (透传到 Python)
     */
    public JsonNode createKnowledgeBase(String kbId, String name, String description, String domain) {
        log.info("Calling Python createKB: kbId={}", kbId);

        return pythonRestClient.post()
                .uri("/api/v1/kb")
                .body(Map.of(
                        "kb_id", kbId,
                        "name", name,
                        "description", description,
                        "domain", domain
                ))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    log.error("Python createKB failed: status={}", resp.getStatusCode());
                    throw BusinessException.upstream(
                            "Python service error: " + resp.getStatusCode());
                })
                .body(JsonNode.class);
    }

    /**
     * 摄入文档
     */
    public JsonNode ingestDocument(String kbId, String filePath) {
        log.info("Calling Python ingest: kbId={}, file={}", kbId, filePath);

        return pythonRestClient.post()
                .uri("/api/v1/ingest")
                .body(Map.of(
                        "kb_id", kbId,
                        "file_path", filePath
                ))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, resp) -> {
                    throw BusinessException.upstream(
                            "Python ingest error: " + resp.getStatusCode());
                })
                .body(JsonNode.class);
    }

    /**
     * 健康检查
     */
    public boolean isHealthy() {
        try {
            JsonNode resp = pythonRestClient.get()
                    .uri("/readyz")
                    .retrieve()
                    .body(JsonNode.class);
            return resp != null && "ready".equals(resp.path("status").asText());
        } catch (Exception e) {
            log.warn("Python health check failed: {}", e.getMessage());
            return false;
        }
    }
}
