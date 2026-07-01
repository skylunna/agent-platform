package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 18:39
 * @description: ChatSessionEntity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin.chat_session")
public class ChatSessionEntity {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private String agentId;

    private String userId;

    private String title;

    private String status;

    private Integer messageCount;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastActiveAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime deletedAt;
}