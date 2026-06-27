package com.tiny.agentplatform.manage.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:23
 * @description: CreateAgentRequest
 */
@Data
public class CreateAgentRequest {

    @NotBlank
    @Size(max = 128)
    private String name;

    private String description;

    @NotBlank
    private String domain = "medical";

    @NotBlank
    private String model = "deepseek-chat";

    @NotNull
    @DecimalMin("0.0") @DecimalMax("2.0")
    private BigDecimal temperature = new BigDecimal("0.10");

    @Min(1) @Max(20)
    private Integer maxIterations = 5;

    @NotBlank
    private String systemPrompt;

    @NotNull
    private List<String> toolsEnabled;

    private List<String> kbIds;   // 绑定的知识库
}
