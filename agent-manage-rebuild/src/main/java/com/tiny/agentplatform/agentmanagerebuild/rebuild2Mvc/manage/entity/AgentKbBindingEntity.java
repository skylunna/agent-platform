package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 16:03
 * @description: AgentKbBindingEntity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin.agent_kb_binding")
public class AgentKbBindingEntity {

    @TableId(value = "agent_id", type = IdType.INPUT)
    private String agentId;

    private String kbId;

    /**
     * 优先级
     */
    private Integer priority;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
}