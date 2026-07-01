package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.ChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:00
 * @Description: ChatSessionService 类功能描述
 */
public interface ChatSessionService {


    SseEmitter chatStream(ChatRequest request);
}
