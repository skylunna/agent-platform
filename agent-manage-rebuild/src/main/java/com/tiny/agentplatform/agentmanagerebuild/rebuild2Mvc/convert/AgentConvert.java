package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.convert;

import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.AgentDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.AgentEntity;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:37
 * @Description: AgentConvert 类功能描述
 */
public class AgentConvert {

    public static AgentDTO convertEntityToDto(AgentEntity entity) {
        return AgentDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .domain(entity.getDomain())
                .model(entity.getModel())
                .temperature(entity.getTemperature())
                .maxIterations(entity.getMaxIterations())
                .systemPrompt(entity.getSystemPrompt())
                .toolsEnabled(entity.getToolsEnabled())
                .status(entity.getStatus())
                .version(entity.getVersion())
                .build();
    }

}
