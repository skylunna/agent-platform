package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 19:35
 * @description: IngestRequest
 */
@Data
public class IngestRequest {

    @NotBlank
    private String filePath;
}