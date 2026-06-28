package com.tiny.agentplatform.manage.repository;


import com.tiny.agentplatform.manage.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:43
 * @description: ChatSessionRepository
 */
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {

    @Query("SELECT s FROM ChatSessionEntity s WHERE s.id = :id AND s.deletedAt IS NULL")
    Optional<ChatSessionEntity> findActiveById(@Param("id") String id);

    @Modifying
    @Query("UPDATE ChatSessionEntity s SET s.lastActiveAt = :now, "
            + "s.messageCount = s.messageCount + 1 WHERE s.id = :id")
    void touchAndIncrement(@Param("id") String id, @Param("now") OffsetDateTime now);
}
