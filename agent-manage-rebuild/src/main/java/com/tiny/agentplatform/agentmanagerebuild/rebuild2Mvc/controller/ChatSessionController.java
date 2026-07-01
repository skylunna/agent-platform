package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.controller;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.ChatRequest;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result.Result;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.ChatSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/30 16:52
 * @Description: ChatSessionController 类功能描述
 */
@RestController
@Tag(name = "会话管理")
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
public class ChatSessionController {


    private final ChatSessionService chatSessionService;


    @Operation(summary = "会话")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@Valid @RequestBody ChatRequest request) {
        return chatSessionService.chatStream(request);
    }
}