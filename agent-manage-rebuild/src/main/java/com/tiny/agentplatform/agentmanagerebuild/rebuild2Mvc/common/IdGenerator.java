package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.common;

import java.util.UUID;

/**
 * Copyright (c) 2026 skuluna. All Rights Reserved.
 *
 * @author: skulunna
 * @date: 2026/6/27 15:12
 * @description: IdGenerator
 *
 *  业务 ID 生成器
 *  风格: {prefix}-{8位小写hex}, 与 Python侧风格一致
 */
public final class IdGenerator {

    private IdGenerator() {}

    public static String agentId() { return generate("agent"); }
    public static String sessionId() { return generate("sess"); }
    public static String chatLogId() { return generate("log"); }


    private static String generate(String prefix) {
        String hex = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return prefix + "-" + hex;
    }
}
