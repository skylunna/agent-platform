package com.tiny.agentplatform.manage.repository;

import com.tiny.agentplatform.manage.entity.AgentKbBindingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:29
 * @description: AgentKbBindingRepository
 */
public interface AgentKbBindingRepository
        extends JpaRepository<AgentKbBindingEntity, AgentKbBindingEntity.Pk> {

    @Query("SELECT b.kbId FROM AgentKbBindingEntity b WHERE b.agentId = :agentId")
    List<String> findKbIdsByAgentId(String agentId);
}