package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.ChatLogDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.ChatLogEntity;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:00
 * @Description: ChatLogService 类功能描述
 */
public interface ChatLogService {


    List<ChatLogDTO> getChatLogs(String sessionId);
}
