package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.AgentDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.CreateAgentRequest;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 16:59
 * @Description: AgentService 类功能描述
 */
public interface AgentService {


    AgentDTO createAgent(CreateAgentRequest request);

    AgentDTO getAgent(String id);

    List<AgentDTO> listAgent();

    AgentDTO activate(String id);

    AgentDTO deleteAgent(String id);

}