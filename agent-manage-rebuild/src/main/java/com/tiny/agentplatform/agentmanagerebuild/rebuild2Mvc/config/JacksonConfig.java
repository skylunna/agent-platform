package com.tiny.agentplatform.agentmanagerebuild.rebuild2Mvc.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 11695
 */
@Configuration
public class JacksonConfig {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 序列化（返回前端）
            builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
            // 反序列化（接收前端）
            builder.deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
        };
    }
}