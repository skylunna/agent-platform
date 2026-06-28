package com.tiny.agentplatform.manage.service.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiny.agentplatform.manage.client.PythonStreamClient;
import com.tiny.agentplatform.manage.common.IdGenerator;
import com.tiny.agentplatform.manage.dto.python.PythonChatRequest;
import com.tiny.agentplatform.manage.dto.request.ChatRequest;
import com.tiny.agentplatform.manage.entity.AgentEntity;
import com.tiny.agentplatform.manage.entity.ChatLogEntity;
import com.tiny.agentplatform.manage.entity.ChatSessionEntity;
import com.tiny.agentplatform.manage.exception.BusinessException;
import com.tiny.agentplatform.manage.repository.AgentKbBindingRepository;
import com.tiny.agentplatform.manage.repository.AgentRepository;
import com.tiny.agentplatform.manage.repository.ChatLogRepository;
import com.tiny.agentplatform.manage.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/28 18:52
 * @description: ChatService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final AgentRepository agentRepository;
    private final AgentKbBindingRepository bindingRepository;
    private final ChatSessionRepository sessionRepository;
    private final ChatLogRepository chatLogRepository;
    private final PythonStreamClient pythonStreamClient;
    private final ObjectMapper objectMapper;

    /**
     * 流式对话入口
     * 返回的 SseEmitter 由 controller 直接返回给 Spring MVC
     */
    public SseEmitter chatStream(ChatRequest req) {
        // 1. 加载 Agent 配置
        AgentEntity agent = agentRepository.findActiveById(req.getAgentId())
                .orElseThrow(() -> BusinessException.notFound(
                        "Agent not found: " + req.getAgentId()));

        if (!"active".equals(agent.getStatus())) {
            throw BusinessException.validation(
                    "Agent is not active: " + agent.getStatus());
        }

        // 2. 加载或创建 session
        ChatSessionEntity session = loadOrCreateSession(
                req.getSessionId(), agent.getId(), req.getUserId());

        // 3. 解析 kb_id (取 Agent 绑定的第一个 KB; 多 KB 留 Phase 2)
        List<String> kbIds = bindingRepository.findKbIdsByAgentId(agent.getId());
        if (kbIds.isEmpty()) {
            throw BusinessException.validation(
                    "Agent has no knowledge base bound: " + agent.getId());
        }
        String primaryKbId = kbIds.get(0);

        // 4. 重建对话历史
        List<PythonChatRequest.Message> messages = buildMessages(
                session.getId(), req.getMessage());

        // 5. 构造发给 Python 的请求
        PythonChatRequest pythonReq = PythonChatRequest.builder()
                .sessionId(session.getId())
                .kbId(primaryKbId)
                .messages(messages)
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
        SseEmitter emitter = new SseEmitter(5 * 60_000L);  // 5min 超时
        ChatEventCollector collector = new ChatEventCollector(objectMapper);
        long startTime = System.currentTimeMillis();

        // 7. 异步处理流
        Flux<PythonStreamClient.SseFrame> flux = pythonStreamClient.chatStream(pythonReq);

        flux
                // SseEmitter.send 是阻塞操作,切到弹性线程池
                .publishOn(Schedulers.boundedElastic())
                .subscribe(
                        frame -> handleFrame(frame, emitter, collector),
                        error -> handleError(error, emitter, collector, session, req, startTime),
                        () -> handleComplete(emitter, collector, session, req, startTime)
                );

        // 8. 客户端断开时,日志记录 (subscribe 的 disposable 此处不细管,Phase 2 完善)
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
                    .data(frame.rawData(), org.springframework.http.MediaType.APPLICATION_JSON));

            // 2. 收集到 collector
            collector.accept(frame.event(), frame.data());
        } catch (IOException e) {
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
                            org.springframework.http.MediaType.APPLICATION_JSON));
        } catch (IOException ignored) {}
        emitter.completeWithError(error);

        // 落审计 (失败也要记)
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
            sessionRepository.touchAndIncrement(session.getId(), OffsetDateTime.now());
        } catch (Exception e) {
            log.warn("Failed to update session timestamp: {}", e.getMessage());
        }

        // 落审计
        persistChatLogAsync(session, req, collector, startTime);
    }

    @Async
    @Transactional
    public void persistChatLogAsync(ChatSessionEntity session,
                                    ChatRequest req,
                                    ChatEventCollector collector,
                                    long startTime) {
        try {
            ChatLogEntity log = new ChatLogEntity();
            log.setId(IdGenerator.chatLogId());
            log.setSessionId(session.getId());
            log.setAgentId(req.getAgentId());
            log.setUserMessage(req.getMessage());
            log.setAssistantMessage(collector.getAnswer());
            log.setExecutionTrace(collector.getTrace());
            log.setCitations(collector.getCitations());
            log.setIterations(collector.getIterations());
            log.setPromptTokens(collector.getPromptTokens());
            log.setCompletionTokens(collector.getCompletionTokens());
            log.setTotalTokens(collector.getTotalTokens());
            log.setDurationMs(System.currentTimeMillis() - startTime);
            log.setStatus(collector.getStatus());
            log.setErrorMessage(collector.getErrorMessage());

            chatLogRepository.save(log);
            ChatService.log.info("Chat log persisted: id={}, session={}, status={}, "
                            + "duration={}ms, tokens={}",
                    log.getId(), session.getId(), log.getStatus(),
                    log.getDurationMs(), log.getTotalTokens());
        } catch (Exception e) {
            ChatService.log.error("Failed to persist chat log for session={}",
                    session.getId(), e);
        }
    }

    @Transactional
    public ChatSessionEntity loadOrCreateSession(String sessionId, String agentId, String userId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return sessionRepository.findActiveById(sessionId)
                    .orElseThrow(() -> BusinessException.notFound(
                            "Session not found: " + sessionId));
        }
        ChatSessionEntity s = new ChatSessionEntity();
        s.setId(IdGenerator.sessionId());
        s.setAgentId(agentId);
        s.setUserId(userId != null ? userId : "anonymous");
        s.setStatus("active");
        s.setMessageCount(0);
        s.setLastActiveAt(OffsetDateTime.now());
        return sessionRepository.save(s);
    }

    /**
     * 重建对话历史: 从 chat_log 取最近 N 条,还原成 OpenAI messages 格式
     * MVP: 取最近 5 轮,Phase 2 用 chat_message 表
     */
    private List<PythonChatRequest.Message> buildMessages(String sessionId, String currentMessage) {
        List<PythonChatRequest.Message> result = new ArrayList<>();
        List<ChatLogEntity> recent = chatLogRepository.findRecentBySessionId(sessionId, 5);
        // recent 是按时间倒序的,需要反转
        for (int i = recent.size() - 1; i >= 0; i--) {
            ChatLogEntity logEntry = recent.get(i);
            result.add(PythonChatRequest.Message.builder()
                    .role("user")
                    .content(logEntry.getUserMessage())
                    .build());
            if (logEntry.getAssistantMessage() != null
                    && !logEntry.getAssistantMessage().isBlank()) {
                result.add(PythonChatRequest.Message.builder()
                        .role("assistant")
                        .content(logEntry.getAssistantMessage())
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