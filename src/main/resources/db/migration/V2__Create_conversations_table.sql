-- Criação da tabela conversations
CREATE TABLE IF NOT EXISTS conversations (
    id VARCHAR(36) PRIMARY KEY,
    conversation_summarization_id VARCHAR(36),
    title VARCHAR(256) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_conversations_created_at ON conversations(created_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_updated_at ON conversations(updated_at ASC);
CREATE INDEX IF NOT EXISTS idx_conversations_conversations_summarization_id ON conversations(conversation_summarization_id);
CREATE INDEX IF NOT EXISTS idx_conversations_conversations_summarization_created ON conversations(conversation_summarization_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_conversations_conversations_summarization_updated ON conversations(conversation_summarization_id, updated_at DESC);
