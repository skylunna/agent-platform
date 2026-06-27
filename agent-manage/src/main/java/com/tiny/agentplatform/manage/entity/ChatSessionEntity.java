package com.tiny.agentplatform.manage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 18:39
 * @description: ChatSessionEntity
 */
@Data
@Entity
@Table(name = "chat_session", schema = "admin")
public class ChatSessionEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "agent_id", nullable = false, length = 64)
    private String agentId;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(length = 256)
    private String title;

    @Column(nullable = false, length = 16)
    private String status;

    @Column(name = "message_count", nullable = false)
    private Integer messageCount;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_active_at", nullable = false)
    private OffsetDateTime lastActiveAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
