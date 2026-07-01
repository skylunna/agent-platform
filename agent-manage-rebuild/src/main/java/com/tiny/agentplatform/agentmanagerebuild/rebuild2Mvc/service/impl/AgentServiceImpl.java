package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.common.AgentStatus;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.common.IdGenerator;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.AgentDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request.CreateAgentRequest;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.exception.BusinessException;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.AgentEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.AgentKbBindingEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.AgentKbBindingMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.AgentMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:01
 * @Description: AgentServiceImpl 类功能描述
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {


    private final AgentMapper agentMapper;
    private final AgentKbBindingMapper agentKbBindingMapper;


    @Override
    @Transactional
    public AgentDTO createAgent(CreateAgentRequest request) {
        AgentEntity agent = new AgentEntity();
        agent.setId(IdGenerator.agentId());
        agent.setName(request.getName());
        agent.setDescription(request.getDescription());
        agent.setDomain(request.getDomain());
        agent.setModel(request.getModel());
        agent.setTemperature(request.getTemperature());
        agent.setMaxIterations(request.getMaxIterations());
        agent.setSystemPrompt(request.getSystemPrompt());
        agent.setToolsEnabled(request.getToolsEnabled());
        agent.setStatus(AgentStatus.DRAFT.value());
        agent.setVersion(1);
        agent.setCreatedBy("system");

        agentMapper.insert(agent);

        // 绑定知识库关系
        if (!CollectionUtils.isEmpty(request.getKbIds())) {
            for (String kbId : request.getKbIds()) {
                AgentKbBindingEntity binding = new AgentKbBindingEntity();
                binding.setAgentId(agent.getId());
                binding.setKbId(kbId);
                binding.setPriority(0);
                agentKbBindingMapper.insert(binding);
            }
        }

        log.info("Agent created: id={}, name={}", agent.getId(), agent.getName());
        return toDto(agent, request.getKbIds());

    }


    @Override
    @Transactional(readOnly = true)
    public AgentDTO getAgent(String id) {
        AgentEntity agent = Optional.ofNullable(
                agentMapper.selectOne(
                        new LambdaQueryWrapper<AgentEntity>()
                                .eq(AgentEntity::getId, id)
                                .isNull(AgentEntity::getDeletedAt)
                )).orElseThrow(() -> BusinessException.notFound("Agent not found: " + id));

        List<String> kdbIds = agentKbBindingMapper.selectList(
                new LambdaQueryWrapper<AgentKbBindingEntity>()
                        .eq(AgentKbBindingEntity::getAgentId, id)
        ).stream().map(AgentKbBindingEntity::getKbId).toList();

        return toDto(agent, kdbIds);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AgentDTO> listAgent(){
        List<AgentEntity> list = Optional.ofNullable(
                agentMapper.selectList(
                        new LambdaQueryWrapper<AgentEntity>()
                                .isNull(AgentEntity::getDeletedAt)
                )
        ).orElseThrow(() -> BusinessException.notFound("Agent not found"));

        return list.stream().map(e -> {
            List<String> kbIds = agentKbBindingMapper.selectList(
                    new LambdaQueryWrapper<AgentKbBindingEntity>()
                            .eq(AgentKbBindingEntity::getAgentId, e.getId())
            ).stream().map(AgentKbBindingEntity::getKbId).toList();
            return toDto(e, kbIds);
        }).toList();
    }


    @Override
    @Transactional
    public AgentDTO activate(String id) {
        AgentEntity agent = Optional.ofNullable(
                agentMapper.selectOne(
                        new LambdaQueryWrapper<AgentEntity>()
                                .eq(AgentEntity::getId, id)
                                .isNull(AgentEntity::getDeletedAt)
                )
        ).orElseThrow(() -> BusinessException.notFound("Agent not found: " + id));
        agent.setStatus(AgentStatus.ACTIVE.value());
        agentMapper.updateById(agent);
        List<String> kbIds = agentKbBindingMapper.selectList(
                new LambdaQueryWrapper<AgentKbBindingEntity>()
                        .eq(AgentKbBindingEntity::getAgentId, id)
        ).stream().map(AgentKbBindingEntity::getKbId).toList();
        if (CollectionUtils.isEmpty(kbIds)) {
            throw BusinessException.notFound("Agent has no knowledge base bound: " + agent.getId());
        }

        return toDto(agent, kbIds);
    }


    @Override
    @Transactional
    public AgentDTO deleteAgent(String id) {
        AgentEntity agent = Optional.ofNullable(
                agentMapper.selectOne(
                        new LambdaQueryWrapper<AgentEntity>()
                                .eq(AgentEntity::getId, id)
                                .isNull(AgentEntity::getDeletedAt)
                )
        ).orElseThrow(() -> BusinessException.notFound("Agent not found: " + id));

        agent.setDeletedAt(LocalDateTime.now());

        agentMapper.updateById(agent);

        List<String> kbIds = agentKbBindingMapper.selectList(
                new LambdaQueryWrapper<AgentKbBindingEntity>()
                        .eq(AgentKbBindingEntity::getAgentId, id)
        ).stream().map(AgentKbBindingEntity::getKbId).toList();

        if (!CollectionUtils.isEmpty(kbIds)) {
            throw BusinessException.notFound("Agent has knowledge base bound: " + agent.getId());
        }
        return toDto(agent, kbIds);
    }


    private AgentDTO toDto(AgentEntity e, List<String> kbIds) {
        return AgentDTO.builder()
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