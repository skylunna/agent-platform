package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.controller;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.AgentDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.CreateAgentRequest;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result.Result;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.AgentService;
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
 * @Date: 2026/6/29 17:25
 * @Description: AgentController 类功能描述
 */
@RestController
@Tag(name = "基础指标表")
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {


    private final AgentService agentService;


    @Operation(summary = "创建Agent")
    @PostMapping("/create")
    public Result<AgentDTO> create(@Valid @RequestBody CreateAgentRequest req) {
        return Result.success(agentService.createAgent(req));
    }


    @Operation(summary = "获取Agent")
    @GetMapping("/get")
    public Result<AgentDTO> get(@RequestParam String id) {
        return Result.success(agentService.getAgent(id));
    }


    @Operation(summary = "获取Agent列表")
    @GetMapping("/list")
    public Result<List<AgentDTO>> list() {
        return Result.success(agentService.listAgent());
    }


    @Operation(summary = "激活Agent")
    @GetMapping("/activate")
    public Result<AgentDTO> activate(@RequestParam String id) {
        return Result.success(agentService.activate(id));
    }

    @Operation(summary = "删除Agent")
    @PostMapping("/delete")
    public Result<AgentDTO> delete(@RequestParam String id) {
        return Result.success(agentService.deleteAgent(id));
    }
}