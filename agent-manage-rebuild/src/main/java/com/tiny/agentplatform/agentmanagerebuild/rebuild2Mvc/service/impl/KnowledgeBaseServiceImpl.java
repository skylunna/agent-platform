package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.dto.KnowledgeBaseDTO;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.entity.KnowledgeBaseEntity;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.manage.mapper.KnowledgeBaseMapper;
import com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/29 17:02
 * @Description: KnowledgeBaseServiceImpl 类功能描述
 */
@Service
@RequiredArgsConstructor
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {


    private final KnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    @Transactional(readOnly = true)
    public List<KnowledgeBaseDTO> list() {
        List<KnowledgeBaseEntity> kbList = knowledgeBaseMapper.selectList(
                new LambdaQueryWrapper<KnowledgeBaseEntity>()
                        .isNull(KnowledgeBaseEntity::getDeletedAt)
                        .orderByDesc(KnowledgeBaseEntity::getCreatedAt)
        );

        return kbList.stream().map(this::convertToDTO).toList();
    }


    private KnowledgeBaseDTO convertToDTO(KnowledgeBaseEntity entity) {
        return KnowledgeBaseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .domain(entity.getDomain())
                .embeddingModel(entity.getEmbeddingModel())
                .embeddingDim(entity.getEmbeddingDim())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
}