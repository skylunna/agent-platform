package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.handler.JsonbTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 15:55
 * @description: AgentEntity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "admin.agent", autoResultMap = true)
public class AgentEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String name;

    private String description;

    private String domain;

    private String model;

    private BigDecimal temperature;

    private Integer maxIterations;

    private String systemPrompt;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> toolsEnabled;

    private String status;

    private Integer version;

    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime deletedAt;
}