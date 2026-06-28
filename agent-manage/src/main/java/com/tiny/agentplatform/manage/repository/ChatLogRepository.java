package com.tiny.agentplatform.manage.repository;

import com.tiny.agentplatform.manage.entity.ChatLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:43
 * @description: ChatLogRepository
 */
public interface ChatLogRepository extends JpaRepository<ChatLogEntity, String> {

    @Query("SELECT l FROM ChatLogEntity l WHERE l.sessionId = :sessionId "
            + "ORDER BY l.createdAt ASC")
    List<ChatLogEntity> findBySessionId(@Param("sessionId") String sessionId);

    /**
     * 取最近 N 轮对话用于上下文重建
     */
    @Query(value = "SELECT * FROM admin.chat_log WHERE session_id = :sessionId "
            + "AND status = 'success' ORDER BY created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<ChatLogEntity> findRecentBySessionId(@Param("sessionId") String sessionId,
                                              @Param("limit") int limit);
}