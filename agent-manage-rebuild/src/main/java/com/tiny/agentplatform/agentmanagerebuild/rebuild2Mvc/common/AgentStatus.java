package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.common;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 15:31
 * @description: AgentStatus
 */
public enum AgentStatus {

    DRAFT,
    ACTIVE,
    ARCHIVED;

    public String value() { return name().toLowerCase(); }

    public static AgentStatus from(String s) {
        return AgentStatus.valueOf(s.toUpperCase());
    }
}
