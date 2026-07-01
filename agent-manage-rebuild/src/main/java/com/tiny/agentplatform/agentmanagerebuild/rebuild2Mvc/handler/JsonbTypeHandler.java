package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Copyright (c) 2026 skyluna. All Rights Reserved.
 *
 * @Author:  skylunna
 * @Date: 2026/6/30 15:31
 * @Description: JsonbTypeHandler 类功能描述
 */
@MappedTypes({Object.class})
public class JsonbTypeHandler extends JacksonTypeHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public JsonbTypeHandler(Class<?> type) {
        super(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, OBJECT_MAPPER.writeValueAsString(parameter), Types.OTHER);
        } catch (JsonProcessingException e) {
            throw new SQLException("Error converting object to JSON string", e);
        }
    }
}