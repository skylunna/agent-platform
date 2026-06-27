package com.tiny.agentplatform.manage.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 16:03
 * @description: AgentKbBindingEntity
 */
@Data
@Entity
@Table(name = "agent_kb_binding", schema = "admin")
@IdClass(AgentKbBindingEntity.Pk.class)
public class AgentKbBindingEntity {

    @Id
    @Column(name = "agent_id", length = 64)
    private String agentId;

    @Id
    @Column(name = "kb_id", length = 64)
    private String kbId;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Data
    public static class Pk implements Serializable {
        private String agentId;
        private String kbId;

        public Pk() {}
        public Pk(String agentId, String kbId) {
            this.agentId = agentId;
            this.kbId = kbId;
        }
    }

}
