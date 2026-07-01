package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.client.PythonExecutionClient;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.KnowledgeBaseDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.CreateKbRequest;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.KnowledgeBaseEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result.Result;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/30 16:54
 * @Description: KnowledgeBaseController 类功能描述
 */
@RestController
@Tag(name = "知识库管理")
@RequestMapping("/api/v1/kb")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final PythonExecutionClient pythonExecutionClient;
    private final KnowledgeBaseService knowledgeBaseService;


    @Operation(summary = "创建会话")
    @PostMapping("/create")
    public Result<JsonNode> create(@Valid @RequestBody CreateKbRequest request) {
        // 透传给 Python，Python写入 rag.knowledge_base
        JsonNode result = pythonExecutionClient
                .createKnowledgeBase(request.getKbId(), request.getName(), request.getDescription(), request.getDomain());
        return Result.success(result);
    }

    @Operation(summary = "会话列表")
    @GetMapping("/list")
    public Result<List<KnowledgeBaseDTO>> list() {
        return Result.success(knowledgeBaseService.list());
    }
}