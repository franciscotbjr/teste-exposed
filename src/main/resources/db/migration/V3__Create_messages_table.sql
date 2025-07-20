-- Criação da tabela messages
CREATE TABLE IF NOT EXISTS messages (
    id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL,
    role VARCHAR(15) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    -- Chaves estrangeiras baseadas nas classes Exposed
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE NO ACTION,
    FOREIGN KEY (role) REFERENCES roles(name) ON DELETE NO ACTION
);

-- Índices para otimizar consultas
CREATE INDEX IF NOT EXISTS idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at ASC);
CREATE INDEX IF NOT EXISTS idx_messages_conversation_created ON messages(conversation_id, created_at ASC);
