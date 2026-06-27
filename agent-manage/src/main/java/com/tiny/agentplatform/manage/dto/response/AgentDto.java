package com.tiny.agentplatform.manage.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:24
 * @description: AgentDto
 */
@Data
@Builder
public class AgentDto {

    private String id;
    private String name;
    private String description;
    private String domain;
    private String model;
    private BigDecimal temperature;
    private Integer maxIterations;
    private String systemPrompt;
    private List<String> toolsEnabled;
    private String status;
    private Integer version;
    private List<String> kbIds;
    private OffsetDateTime deletedAt;
}
