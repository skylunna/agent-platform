package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:44
 * @description: ChatRequest
 */
@Data
public class ChatRequest {

    /** 会话 ID,首次对话可不传,Java 自动创建 */
    private String sessionId;

    @NotBlank
    private String agentId;

    @NotBlank
    private String message;

    /** 用户 ID,MVP 默认 anonymous */
    private String userId = "anonymous";
}