package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.result;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author: skylunna
 * @Date: 2026/5/26 16:19
 * @Description: Result 类功能描述
 */
@Data
//@Schema(description = "统一响应结果")
public class Result<T> implements Serializable {

//    @Schema(description = "状态码,200表示成功")
    private Integer code;

//    @Schema(description = "响应消息")
    private String message;

//    @Schema(description = "响应数据")
    private T data;

//    @Schema(description = "时间戳")
    private LocalDateTime timestamp;

    public Result() {
        this.timestamp = LocalDateTime.now();
    }


    // 判断是否成功
    public boolean isSuccess() {
        return Integer.valueOf(200).equals(this.code);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 新增：通过枚举返回成功响应
    public static <T> Result<T> success(ResultCode resultCode) {
        return success(resultCode, null);
    }

    // 新增：枚举 + 返回数据
    public static <T> Result<T> success(ResultCode resultCode, T data) {
        Result<T> result = new Result<>();
        result.setCode(resultCode.getCode());
        result.setMessage(resultCode.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // 新增：直接接收ResultCode枚举的重载方法
    public static <T> Result<T> error(ResultCode resultCode) {
        return error(resultCode.getCode(), resultCode.getMessage());
    }
}