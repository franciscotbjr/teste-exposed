-- Criação da tabela messages
CREATE TABLE IF NOT EXISTS conversations_summarizations (
    id VARCHAR(36) PRIMARY KEY,
    origin_conversation_id VARCHAR(36) NOT NULL,
    destiny_conversation_id VARCHAR(36),
    summary TEXT NOT NULL,
    tokens_used INTEGER NOT NULL DEFAULT 0,
    summary_method VARCHAR(50) NOT NULL DEFAULT 'deepseek',
    is_active BOOLEAN NOT NULL DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Chaves estrangeiras baseadas nas classes Exposed
    FOREIGN KEY (origin_conversation_id) REFERENCES conversations(id) ON DELETE NO ACTION,
    FOREIGN KEY (destiny_conversation_id) REFERENCES conversations(id) ON DELETE NO ACTION
);

-- Índices para otimizar consultas
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_origin_conversation_id ON conversations_summarizations(origin_conversation_id);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_destiny_conversation_id ON conversations_summarizations(destiny_conversation_id);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_created_at ON conversations_summarizations(created_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_updated_at ON conversations_summarizations(updated_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_is_active ON conversations_summarizations(is_active);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_summary_method ON conversations_summarizations(summary_method);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_origin_conversation_created ON conversations_summarizations(origin_conversation_id, created_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_destiny_conversation_created ON conversations_summarizations(destiny_conversation_id, created_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_origin_conversation_updated ON conversations_summarizations(origin_conversation_id, updated_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_summarizations_destiny_conversation_updated ON conversations_summarizations(destiny_conversation_id, updated_at ASC);
