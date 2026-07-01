package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:37
 * @description: CreateKbRequest
 */
@Data
public class CreateKbRequest {

    @NotBlank
    private String kbId;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String domain;

}
