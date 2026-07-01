package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.controller;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.ChatLogDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.ChatLogEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result.Result;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.ChatLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/30 16:32
 * @Description: SessionController 类功能描述
 */
@RestController
@Tag(name = "聊天日志")
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class ChatLogController {


    private final ChatLogService chatLogService;


    @Operation(summary = "获取会话日志列表")
    @GetMapping("/logs")
    public Result<List<ChatLogDTO>> logs(@RequestParam String sessionId) {
        return Result.success(chatLogService.getChatLogs(sessionId));
    }
}