package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/30 17:12
 * @Description: KnowledgeBaseDTO 类功能描述
 */
@Data
@Builder
public class KnowledgeBaseDTO {

    private String id;

    private String name;

    private String description;

    private String domain;

    private String embeddingModel;

    private Integer embeddingDim;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    private OffsetDateTime deletedAt;
}