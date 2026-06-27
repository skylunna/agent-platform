package com.tiny.agentplatform.manage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 18:27
 * @description: KnowledgeBaseEntity
 *
 *  知识库实体 - 只读视图
 *  实际数据由 Python 侧维护, Java 仅读取展示
 */
@Data
@Entity
@Immutable      // Hibernate 层禁止修改
@Table(name = "knowledge_base", schema = "rag")
public class KnowledgeBaseEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String domain;

    @Column(name = "embedding_model", nullable = false)
    private String embeddingModel;

    @Column(name = "embedding_dim", nullable = false)
    private Integer embeddingDim;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

}
