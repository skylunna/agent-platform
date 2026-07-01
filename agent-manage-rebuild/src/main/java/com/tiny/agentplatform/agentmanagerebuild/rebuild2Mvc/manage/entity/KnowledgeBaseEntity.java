package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

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
@TableName(value = "knowledge_base", schema = "rag")
public class KnowledgeBaseEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String name;

    private String description;

    private String domain;

    private String embeddingModel;

    private Integer embeddingDim;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private OffsetDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private OffsetDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private OffsetDateTime deletedAt;
}