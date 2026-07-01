package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.config.JsonbTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:41
 * @description: ChatLogEntity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "admin.chat_log", autoResultMap = true)
public class ChatLogEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String sessionId;

    private String agentId;

    private String userMessage;

    private String assistantMessage;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private JsonNode executionTrace;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private JsonNode citations;

    private Integer iterations;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    private Long durationMs;

    private String status;

    private String errorMessage;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
}