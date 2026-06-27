package com.tiny.agentplatform.manage.repository;

import com.tiny.agentplatform.manage.entity.KnowledgeBaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:35
 * @description: KnowledgeBaseRepository
 */
public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBaseEntity, String> {

    @Query("SELECT k FROM KnowledgeBaseEntity k WHERE k.deletedAt IS NULL ORDER BY k.createdAt DESC")
    List<KnowledgeBaseEntity> findAllActive();
}