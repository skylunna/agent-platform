package com.tiny.agentplatform.manage.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tiny.agentplatform.manage.client.PythonExecutionClient;
import com.tiny.agentplatform.manage.common.ApiResponse;
import com.tiny.agentplatform.manage.dto.request.CreateKbRequest;
import com.tiny.agentplatform.manage.dto.request.IngestRequest;
import com.tiny.agentplatform.manage.entity.KnowledgeBaseEntity;
import com.tiny.agentplatform.manage.repository.KnowledgeBaseRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:35
 * @description: KnowledgeBaseController
 */
@RestController
@RequestMapping("/api/v1/kb")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final PythonExecutionClient pythonClient;
    private final KnowledgeBaseRepository kbRepository;

    @PostMapping
    public ApiResponse<JsonNode> create(@Valid @RequestBody CreateKbRequest req) {
        // 透传给 Python,Python 写入 rag.knowledge_base
        JsonNode result = pythonClient.createKnowledgeBase(
                req.getKbId(), req.getName(), req.getDescription(), req.getDomain());
        return ApiResponse.ok(result);
    }

    @GetMapping
    public ApiResponse<List<KnowledgeBaseEntity>> list() {
        // 直接查 rag.knowledge_base
        return ApiResponse.ok(kbRepository.findAllActive());
    }

    @PostMapping("/{kbId}/documents")
    public ApiResponse<JsonNode> ingestDocument(
            @PathVariable String kbId,
            @RequestBody IngestRequest req) {
        JsonNode result = pythonClient.ingestDocument(kbId, req.getFilePath());
        return ApiResponse.ok(result);
    }
}
