package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result;

import lombok.Getter;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/6/8 15:56
 * @Description: ResultCode 类功能描述
 *
 *  全局响应码
 *  编码规范:
 *      200xx 成功
 *      400xx 客户端错误 (参数、业务规则不满足)
 *      401xx 认证/授权
 *      500xx 服务端错误
 */
@Getter
public enum ResultCode {

    // ===== 成功 =====
    SUCCESS(20000, "操作成功"),

    // ===== 通用客户端错误 =====
    PARAM_ERROR(40000, "参数错误"),
    NOT_FOUND(40004, "资源不存在"),
    SYSTEM_BUSY(40009, "系统繁忙,请稍后重试"),

    // ===== 认证授权 =====
    UNAUTHORIZED(40100, "未登录或登录已过期"),
    FORBIDDEN(40300, "无权限访问"),

    // ===== 用户域 =====
    USER_NOT_FOUND(40010, "用户不存在"),

    // ===== 服务端错误 =====
    SYSTEM_ERROR(50000, "系统内部错误"),
    STOCK_INCONSISTENT(50010, "库存数据异常,请稍后重试"),
    SERVICE_UNAVAILABLE(50020, "Feign接口异常");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}