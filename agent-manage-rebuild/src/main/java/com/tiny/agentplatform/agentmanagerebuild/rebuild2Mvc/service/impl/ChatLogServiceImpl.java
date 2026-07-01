package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.ChatLogDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.exception.BusinessException;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.ChatLogEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.ChatLogMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.ChatLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:01
 * @Description: ChatLogServiceImpl 类功能描述
 */
@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {

    private final ChatLogMapper chatLogMapper;


    @Override
    @Transactional(readOnly = true)
    public List<ChatLogDTO> getChatLogs(String sessionId) {
        List<ChatLogEntity> logs = chatLogMapper.selectList(
                new LambdaQueryWrapper<ChatLogEntity>()
                        .eq(ChatLogEntity::getSessionId, sessionId)
                        .orderByAsc(ChatLogEntity::getCreatedAt)
        );

        if (CollectionUtils.isEmpty(logs)) {
            throw BusinessException.notFound("Chat logs not found for agent: " + sessionId);
        }

        return logs.stream().map(this::entityToDto).toList();
    }


    private ChatLogDTO entityToDto(ChatLogEntity entity) {
        return ChatLogDTO.builder()
                .id(entity.getId())
                .sessionId(entity.getSessionId())
                .agentId(entity.getAgentId())
                .userMessage(entity.getUserMessage())
                .assistantMessage(entity.getAssistantMessage())
                .executionTrace(entity.getExecutionTrace())
                .citations(entity.getCitations())
                .iterations(entity.getIterations())
                .promptTokens(entity.getPromptTokens())
                .completionTokens(entity.getCompletionTokens())
                .totalTokens(entity.getTotalTokens())
                .durationMs(entity.getDurationMs())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}