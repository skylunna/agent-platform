package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.client.ChatEventCollector;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.client.PythonStreamClient;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.common.IdGenerator;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.AgentDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.ChatRequest;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.PythonChatRequest;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.exception.BusinessException;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.AgentKbBindingEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.ChatLogEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.ChatSessionEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.AgentKbBindingMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.ChatLogMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.ChatSessionMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.AgentService;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:01
 * @Description: ChatSessionServiceImpl 类功能描述
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSessionServiceImpl implements ChatSessionService {

    private final ObjectMapper objectMapper;
    private final PythonStreamClient pythonStreamClient;

    private final AgentService agentService;
    private final ChatSessionMapper chatSessionMapper;
    private final AgentKbBindingMapper agentKbBindingMapper;

    private final ChatLogMapper chatLogMapper;

    @Override
    public SseEmitter chatStream(ChatRequest request) {
        // 1. 加载 Agent 配置
        AgentDTO agent = Optional.ofNullable(
                agentService.getAgent(request.getAgentId())
        ).orElseThrow(() -> BusinessException.notFound("Agent not found: " + request.getAgentId()));

        // 1.1 验证 Agent 状态
        if (!"active".equals(agent.getStatus())) {
            throw BusinessException.validation("Agent is not active: " + request.getAgentId());
        }

        // 2. 加载或创建 Session
        ChatSessionEntity session = loadOrCreateSession(request.getSessionId(), request.getAgentId(), request.getUserId());

        // 3. 解析 kb_id 暂时取 Agent 绑定的第一个 KB;
        List<String> kbIds = agentKbBindingMapper.selectList(
                new LambdaQueryWrapper<AgentKbBindingEntity>()
                        .eq(AgentKbBindingEntity::getAgentId, request.getAgentId())
        ).stream().map(AgentKbBindingEntity::getKbId).toList();

        if (CollectionUtils.isEmpty(kbIds)) {
            throw BusinessException.notFound("No KB bound to the agent: " + request.getAgentId());
        }

        String primaryKbId = kbIds.getFirst();

        // 4. 重建对话历史
        List<PythonChatRequest.Message> messages = buildMessages(session.getId(), request.getMessage());

        // 5. 构造发给 Python 的请求
        PythonChatRequest chatRequest = PythonChatRequest.builder()
                .sessionId(session.getId())
                .messages(messages)
                .kbId(primaryKbId)
                .stream(true)
                .agentConfig(PythonChatRequest.AgentConfig.builder()
                        .agentId(agent.getId())
                        .systemPrompt(agent.getSystemPrompt())
                        .model(agent.getModel())
                        .temperature(agent.getTemperature())
                        .maxIterations(agent.getMaxIterations())
                        .toolsEnabled(agent.getToolsEnabled())
                        .build())
                .build();

        // 6. 建立 SseEmitter
        // 5min 超时
        SseEmitter emitter = new SseEmitter(5 * 60_000L);
        ChatEventCollector collector = new ChatEventCollector(objectMapper);
        long startTime = System.currentTimeMillis();

        // 7. 异步处理流
        Flux<PythonStreamClient.SseFrame> flux = pythonStreamClient.chatStream(chatRequest);
        flux
                // SseEmitter.send 是阻塞操作,切到弹性线程池
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        frame -> handleFrame(frame, emitter, collector),
                        error -> handleError(error, emitter, collector, session, request, startTime),
                        () -> handleComplete(emitter, collector, session, request, startTime)
                );

        // 8. 客户端断开时,日志记录 (subscribe 的 disposable 此处不细管)
        emitter.onTimeout(() -> {
            log.warn("SSE timeout for session={}", session.getId());
            emitter.complete();
        });
        emitter.onError(e -> log.error("SSE error for session={}: {}",
                session.getId(), e.getMessage()));

        return emitter;
    }


    private void handleFrame(PythonStreamClient.SseFrame frame,
                             SseEmitter emitter,
                             ChatEventCollector collector) {
        try {
            // 1. 转发给前端 (用原始 JSON 字符串,避免再次序列化)
            emitter.send(SseEmitter.event()
                    .name(frame.event())
                    .data(frame.rawData(), MediaType.APPLICATION_JSON));
            collector.accept(frame.event(), frame.data());
        }  catch (IOException e) {
            log.warn("Failed to send SSE frame (client may have disconnected): {}",
                    e.getMessage());
        }
    }

    private void handleError(Throwable error,
                             SseEmitter emitter,
                             ChatEventCollector collector,
                             ChatSessionEntity session,
                             ChatRequest req,
                             long startTime) {
        log.error("Python stream failed", error);

        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data("{\"message\":\"Upstream error: " + error.getMessage() + "\"}",
                            MediaType.APPLICATION_JSON));
        } catch (IOException ignored) {}

        emitter.completeWithError(error);

        // 落审计
        collector.accept("error", objectMapper.createObjectNode()
                .put("message", error.getMessage()));
        persistChatLogAsync(session, req, collector, startTime);
    }

    private void handleComplete(SseEmitter emitter,
                                ChatEventCollector collector,
                                ChatSessionEntity session,
                                ChatRequest req,
                                long startTime) {
        emitter.complete();

        // 更新 session 活跃时间
        try {
            chatSessionMapper.update(
                    new LambdaUpdateWrapper<ChatSessionEntity>()
                            .set(ChatSessionEntity::getLastActiveAt, LocalDateTime.now())
                            .setSql("message_count = message_count + " + collector.getIterations())
                            .eq(ChatSessionEntity::getId, session.getId())
            );
        } catch (Exception e) {
            log.warn("Failed to update session timestamp: {}", e.getMessage());
        }

        // 落审计
        persistChatLogAsync(session, req, collector, startTime);
    }

    @Transactional
    public ChatSessionEntity loadOrCreateSession(String sessionId, String agentId, String userId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return Optional.ofNullable(
                    chatSessionMapper.selectById(sessionId)
            ).orElseThrow(() -> BusinessException.notFound("Session not found: " + sessionId));
        }

        ChatSessionEntity entity = new ChatSessionEntity();
        entity.setId(IdGenerator.sessionId());
        entity.setAgentId(agentId);
        entity.setUserId(userId);
        entity.setStatus("active");
        entity.setMessageCount(0);
        entity.setLastActiveAt(LocalDateTime.now());
        chatSessionMapper.insert(entity);

        return entity;
    }

    @Async
    @Transactional
    public void persistChatLogAsync(ChatSessionEntity session,
                                    ChatRequest req,
                                    ChatEventCollector collector,
                                    long startTime) {
        try {
            ChatLogEntity logEntity = new ChatLogEntity();
            logEntity.setId(IdGenerator.chatLogId());
            logEntity.setSessionId(session.getId());
            logEntity.setAgentId(req.getAgentId());
            logEntity.setUserMessage(req.getMessage());
            logEntity.setAssistantMessage(collector.getAnswer());
            logEntity.setExecutionTrace(collector.getTrace());
            logEntity.setCitations(collector.getCitations());
            logEntity.setIterations(collector.getIterations());
            logEntity.setPromptTokens(collector.getPromptTokens());
            logEntity.setCompletionTokens(collector.getCompletionTokens());
            logEntity.setTotalTokens(collector.getTotalTokens());
            logEntity.setDurationMs(System.currentTimeMillis() - startTime);
            logEntity.setStatus(collector.getStatus());
            logEntity.setErrorMessage(collector.getErrorMessage());
            chatLogMapper.insert(logEntity);

            log.info("Chat log persisted: id={}, session={}, status={}, "
                            + "duration={}ms, tokens={}",
                    logEntity.getId(), session.getId(), logEntity.getStatus(),
                    logEntity.getDurationMs(), logEntity.getTotalTokens());
        } catch (Exception e) {
            log.error("Failed to persist chat log for session={}",
                    session.getId(), e);
        }
    }


    private List<PythonChatRequest.Message> buildMessages(String sessionId, String currentMessage) {
        List<PythonChatRequest.Message> result = new ArrayList<>();
        List<ChatLogEntity> recent = chatLogMapper.selectList(
                new LambdaQueryWrapper<ChatLogEntity>()
                        .eq(ChatLogEntity::getSessionId, sessionId)
                        .eq(ChatLogEntity::getStatus, "success")
                        .orderByDesc(ChatLogEntity::getCreatedAt)
                        .last("limit 5")
        );

        // recent 时间倒叙，反转
        for (int i = recent.size() - 1; i >= 0; i--) {
            ChatLogEntity entity = recent.get(i);
            result.add(PythonChatRequest.Message.builder()
                    .role("user")
                    .content(entity.getUserMessage())
                    .build());
            if (entity.getAssistantMessage() != null && !entity.getAssistantMessage().isBlank()) {
                result.add(PythonChatRequest.Message.builder()
                        .role("assistant")
                        .content(entity.getAssistantMessage())
                        .build());
            }
        }

        result.add(PythonChatRequest.Message.builder()
                .role("user")
                .content(currentMessage)
                .build());

        return result;
    }
}