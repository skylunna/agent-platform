-- =====================================================
-- Admin Schema 初始化 (Java 管理面)
-- 设计原则:
--   1. UUID 主键 (业务 ID,与 Python 侧风格一致)
--   2. 软删除 + 创建/更新时间
--   3. JSONB 用于灵活配置
--   4. 引用 rag schema 的表 (外键)
-- =====================================================

CREATE SCHEMA IF NOT EXISTS admin;
SET search_path TO admin, public;

-- -------- 1. Agent 表 --------
CREATE TABLE admin.agent (
                             id              VARCHAR(64)  PRIMARY KEY,
                             name            VARCHAR(128) NOT NULL,
                             description     TEXT,
                             domain          VARCHAR(32)  NOT NULL DEFAULT 'medical',

    -- LLM 配置
                             model           VARCHAR(64)  NOT NULL DEFAULT 'deepseek-chat',
                             temperature     NUMERIC(3,2) NOT NULL DEFAULT 0.10,
                             max_iterations  INT          NOT NULL DEFAULT 5,

    -- Prompt 与工具
                             system_prompt   TEXT         NOT NULL,
                             tools_enabled   JSONB        NOT NULL DEFAULT '[]'::jsonb,

    -- 状态
                             status          VARCHAR(16)  NOT NULL DEFAULT 'draft',  -- draft / active / archived
                             version         INT          NOT NULL DEFAULT 1,

    -- 审计
                             created_by      VARCHAR(64)  NOT NULL DEFAULT 'system',
                             created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                             updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                             deleted_at      TIMESTAMPTZ
);

CREATE INDEX idx_agent_status ON admin.agent(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_agent_domain ON admin.agent(domain) WHERE deleted_at IS NULL;

COMMENT ON COLUMN admin.agent.tools_enabled IS '允许的工具名列表,JSON数组';
COMMENT ON COLUMN admin.agent.status IS 'draft=草稿,active=激活,archived=归档';

-- -------- 2. Agent 与 KB 的多对多绑定 --------
CREATE TABLE admin.agent_kb_binding (
                                        agent_id     VARCHAR(64) NOT NULL REFERENCES admin.agent(id),
                                        kb_id        VARCHAR(64) NOT NULL,    -- 引用 rag.knowledge_base.id
                                        priority     INT         NOT NULL DEFAULT 0,  -- 多 KB 时的优先级
                                        created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                        PRIMARY KEY (agent_id, kb_id)
);

CREATE INDEX idx_akb_kb ON admin.agent_kb_binding(kb_id);

COMMENT ON COLUMN admin.agent_kb_binding.kb_id IS '关联 rag.knowledge_base.id,不加外键约束(跨 schema 软关联,便于运维)';

-- -------- 3. 会话表 --------
CREATE TABLE admin.chat_session (
                                    id            VARCHAR(64)  PRIMARY KEY,
                                    agent_id      VARCHAR(64)  NOT NULL REFERENCES admin.agent(id),
                                    user_id       VARCHAR(64)  NOT NULL DEFAULT 'anonymous',
                                    title         VARCHAR(256),

                                    status        VARCHAR(16)  NOT NULL DEFAULT 'active',  -- active / closed
                                    message_count INT          NOT NULL DEFAULT 0,

                                    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
                                    last_active_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                    deleted_at    TIMESTAMPTZ
);

CREATE INDEX idx_session_user ON admin.chat_session(user_id, last_active_at DESC)
    WHERE deleted_at IS NULL;
CREATE INDEX idx_session_agent ON admin.chat_session(agent_id, created_at DESC)
    WHERE deleted_at IS NULL;

-- -------- 4. 调用日志表 (审计的核心) --------
CREATE TABLE admin.chat_log (
                                id                VARCHAR(64)  PRIMARY KEY,
                                session_id        VARCHAR(64)  NOT NULL REFERENCES admin.chat_session(id),
                                agent_id          VARCHAR(64)  NOT NULL,

    -- 输入
                                user_message      TEXT         NOT NULL,

    -- 输出
                                assistant_message TEXT,

    -- Agent 执行轨迹 (JSONB: 完整事件流)
    -- 包含 tool_call / tool_result / 检索 chunk 等
                                execution_trace   JSONB        NOT NULL DEFAULT '[]'::jsonb,

    -- 引用列表 (从 trace 提取,便于查询)
                                citations         JSONB        NOT NULL DEFAULT '[]'::jsonb,

    -- 性能指标
                                iterations        INT,
                                prompt_tokens     INT,
                                completion_tokens INT,
                                total_tokens      INT,
                                duration_ms       BIGINT,

    -- 状态
                                status            VARCHAR(16)  NOT NULL,  -- success / failed / timeout
                                error_message     TEXT,

                                created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chatlog_session ON admin.chat_log(session_id, created_at);
CREATE INDEX idx_chatlog_agent ON admin.chat_log(agent_id, created_at DESC);
CREATE INDEX idx_chatlog_status ON admin.chat_log(status, created_at DESC);

COMMENT ON TABLE admin.chat_log IS '请求级审计日志,一次完整的Agent调用对应一条';
COMMENT ON COLUMN admin.chat_log.execution_trace IS '完整事件流,用于回放和调试';

-- -------- 5. 触发器: 自动更新 updated_at --------
CREATE OR REPLACE FUNCTION admin.touch_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_agent_updated_at BEFORE UPDATE ON admin.agent
    FOR EACH ROW EXECUTE FUNCTION admin.touch_updated_at();