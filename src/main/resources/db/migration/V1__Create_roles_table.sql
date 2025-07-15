-- Criação da tabela roles e inserção dos dados iniciais
CREATE TABLE IF NOT EXISTS roles (
    name VARCHAR(15) PRIMARY KEY,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Inserir os roles padrão com timestamp específico
INSERT OR IGNORE INTO roles (name, created_at) VALUES 
('SYSTEM', '2024-01-01 00:00:00'),
('USER', '2024-01-01 00:00:01'),
('ASSISTANT', '2024-01-01 00:00:02');
