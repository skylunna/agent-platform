package com.tiny.agentplatform.manage.repository;

import com.tiny.agentplatform.manage.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 18:42
 * @description: AgentRepository
 */
public interface AgentRepository extends JpaRepository<AgentEntity, String> {

    @Query("SELECT a FROM AgentEntity a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<AgentEntity> findActiveById(String id);

    @Query("SELECT a FROM AgentEntity a WHERE a.deletedAt IS NULL ORDER BY a.createdAt DESC")
    List<AgentEntity> findAllActive();

    @Query("SELECT a FROM AgentEntity a WHERE a.status = :status AND a.deletedAt IS NULL")
    List<AgentEntity> findByStatus(String status);
}
