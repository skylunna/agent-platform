package com.tiny.agentplatform.manage.controller;


import com.tiny.agentplatform.manage.common.ApiResponse;
import com.tiny.agentplatform.manage.entity.ChatLogEntity;
import com.tiny.agentplatform.manage.repository.ChatLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:55
 * @description: SessionController
 */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final ChatLogRepository chatLogRepository;

    @GetMapping("/{sessionId}/logs")
    public ApiResponse<List<ChatLogEntity>> logs(@PathVariable String sessionId) {
        return ApiResponse.ok(chatLogRepository.findBySessionId(sessionId));
    }
}
