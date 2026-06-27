package com.tiny.agentplatform.manage.controller;

import com.tiny.agentplatform.manage.common.ApiResponse;
import com.tiny.agentplatform.manage.dto.request.CreateAgentRequest;
import com.tiny.agentplatform.manage.dto.response.AgentDto;
import com.tiny.agentplatform.manage.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:32
 * @description: AgentController
 */
@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public ApiResponse<AgentDto> create(@Valid @RequestBody CreateAgentRequest req) {
        return ApiResponse.ok(agentService.createAgent(req));
    }

    @GetMapping("/{id}")
    public ApiResponse<AgentDto> get(@PathVariable String id) {
        return ApiResponse.ok(agentService.getAgent(id));
    }

    @GetMapping
    public ApiResponse<List<AgentDto>> list() {
        return ApiResponse.ok(agentService.listAgents());
    }

    @PostMapping("/{id}/activate")
    public ApiResponse<AgentDto> activate(@PathVariable String id) {
        return ApiResponse.ok(agentService.activate(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable String id) {
        agentService.deleteAgent(id);
        return ApiResponse.ok();
    }
}
