package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.exception;

import lombok.Getter;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 15:34
 * @description: BusinessException
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final int httpStatus;


    public BusinessException(String code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public static BusinessException notFound(String message) {
        return new BusinessException("NOT_FOUND", message, 404);
    }

    public static BusinessException validation(String message) {
        return new BusinessException("VALIDATION_ERROR", message, 400);
    }

    public static BusinessException conflict(String message) {
        return new BusinessException("CONFLICT", message, 409);
    }

    public static BusinessException upstream(String message) {
        return new BusinessException("UPSTREAM_ERROR", message, 502);
    }
}
