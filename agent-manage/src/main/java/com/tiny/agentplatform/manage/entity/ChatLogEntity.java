package com.tiny.agentplatform.manage.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:41
 * @description: ChatLogEntity
 */
@Data
@Entity
@Table(name = "chat_log", schema = "admin")
public class ChatLogEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    @Column(name = "agent_id", nullable = false, length = 64)
    private String agentId;

    @Column(name = "user_message", nullable = false, columnDefinition = "text")
    private String userMessage;

    @Column(name = "assistant_message", columnDefinition = "text")
    private String assistantMessage;

    @Type(JsonBinaryType.class)
    @Column(name = "execution_trace", columnDefinition = "jsonb", nullable = false)
    private JsonNode executionTrace;

    @Type(JsonBinaryType.class)
    @Column(name = "citations", columnDefinition = "jsonb", nullable = false)
    private JsonNode citations;

    private Integer iterations;

    @Column(name = "prompt_tokens")
    private Integer promptTokens;

    @Column(name = "completion_tokens")
    private Integer completionTokens;

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

}
