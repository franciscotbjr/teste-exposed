-- Criação da tabela api_raw_responses
CREATE TABLE IF NOT EXISTS api_raw_responses (
    id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL,
    raw_json TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Chave estrangeira baseada na classe Exposed
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE NO ACTION
);

-- Índices para otimizar consultas
CREATE INDEX IF NOT EXISTS idx_api_raw_responses_conversation_id ON api_raw_responses(conversation_id);
CREATE INDEX IF NOT EXISTS idx_api_raw_responses_created_at ON api_raw_responses(created_at DESC);
