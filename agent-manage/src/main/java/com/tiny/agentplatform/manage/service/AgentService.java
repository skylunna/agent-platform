package com.tiny.agentplatform.manage.service;

import com.tiny.agentplatform.manage.common.AgentStatus;
import com.tiny.agentplatform.manage.common.IdGenerator;
import com.tiny.agentplatform.manage.dto.request.CreateAgentRequest;
import com.tiny.agentplatform.manage.dto.response.AgentDto;
import com.tiny.agentplatform.manage.entity.AgentEntity;
import com.tiny.agentplatform.manage.entity.AgentKbBindingEntity;
import com.tiny.agentplatform.manage.exception.BusinessException;
import com.tiny.agentplatform.manage.repository.AgentKbBindingRepository;
import com.tiny.agentplatform.manage.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:27
 * @description: AgentService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentService {

    private final AgentRepository agentRepository;
    private final AgentKbBindingRepository bindingRepository;


    @Transactional
    public AgentDto createAgent(CreateAgentRequest req) {
        AgentEntity agent = new AgentEntity();
        agent.setId(IdGenerator.agentId());
        agent.setName(req.getName());
        agent.setDescription(req.getDescription());
        agent.setDomain(req.getDomain());
        agent.setModel(req.getModel());
        agent.setTemperature(req.getTemperature());
        agent.setMaxIterations(req.getMaxIterations());
        agent.setSystemPrompt(req.getSystemPrompt());
        agent.setToolsEnabled(req.getToolsEnabled());
        agent.setStatus(AgentStatus.DRAFT.value());
        agent.setVersion(1);
        agent.setCreatedBy("system");

        agentRepository.save(agent);

        // 绑定知识库
        if (req.getKbIds() != null) {
            for (String kbId : req.getKbIds()) {
                AgentKbBindingEntity binding = new AgentKbBindingEntity();
                binding.setAgentId(agent.getId());
                binding.setKbId(kbId);
                binding.setPriority(0);
                bindingRepository.save(binding);
            }
        }

        log.info("Agent created: id={}, name={}", agent.getId(), agent.getName());
        return toDto(agent, req.getKbIds());
    }

    @Transactional(readOnly = true)
    public AgentDto getAgent(String id) {
        AgentEntity agent = agentRepository.findActiveById(id)
                .orElseThrow(() -> BusinessException.notFound("Agent not found: " + id));
        List<String> kbIds = bindingRepository.findKbIdsByAgentId(id);
        return toDto(agent, kbIds);
    }

    @Transactional(readOnly = true)
    public List<AgentDto> listAgents() {
        return agentRepository.findAllActive().stream()
                .map(a -> {
                    List<String> kbIds = bindingRepository.findKbIdsByAgentId(a.getId());
                    return toDto(a, kbIds);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public AgentDto activate(String id) {
        AgentEntity agent = agentRepository.findActiveById(id)
                .orElseThrow(() -> BusinessException.notFound("Agent not found: " + id));
        agent.setStatus(AgentStatus.ACTIVE.value());
        agentRepository.save(agent);
        return toDto(agent, bindingRepository.findKbIdsByAgentId(id));
    }

    @Transactional
    public void deleteAgent(String id) {
        AgentEntity agent = agentRepository.findActiveById(id)
                .orElseThrow(() -> BusinessException.notFound("Agent not found: " + id));
        agent.setDeletedAt(OffsetDateTime.now());
        agentRepository.save(agent);
    }

    private AgentDto toDto(AgentEntity e, List<String> kbIds) {
        return AgentDto.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .domain(e.getDomain())
                .model(e.getModel())
                .temperature(e.getTemperature())
                .maxIterations(e.getMaxIterations())
                .systemPrompt(e.getSystemPrompt())
                .toolsEnabled(e.getToolsEnabled())
                .status(e.getStatus())
                .version(e.getVersion())
                .kbIds(Optional.ofNullable(kbIds).orElse(List.of()))
                .deletedAt(OffsetDateTime.now(ZoneOffset.of("+8")))
                .build();
    }
}
