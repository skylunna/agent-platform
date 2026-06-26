-- 插入一个默认 Agent,便于联调
INSERT INTO admin.agent (
    id, name, description, domain,
    model, temperature, max_iterations,
    system_prompt, tools_enabled, status
) VALUES (
             'agent-diabetes-default',
             '糖尿病诊疗助手',
             '基于《中国2型糖尿病防治指南》的临床决策辅助',
             'medical',
             'deepseek-chat',
             0.10,
             5,
             '你是一名临床决策支持助手 (CDSS Assistant),为执业医生提供基于循证医学的辅助参考。详细规则见 Agent 配置文档。',
             '["search_knowledge_base", "get_patient_context"]'::jsonb,
             'active'
         );

-- 把这个 Agent 绑定到 Step 3 已创建的 kb-diabetes-v1
INSERT INTO admin.agent_kb_binding (agent_id, kb_id, priority)
VALUES ('agent-diabetes-default', 'kb-diabetes-v1', 0);