package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/30 16:40
 * @Description: ChatLogDTO 类功能描述
 */
@Data
@Builder
public class ChatLogDTO {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String sessionId;

    private String agentId;

    private String userMessage;

    private String assistantMessage;

    private JsonNode executionTrace;

    private JsonNode citations;

    private Integer iterations;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    private Long durationMs;

    private String status;

    private String errorMessage;

    private LocalDateTime createdAt;
}