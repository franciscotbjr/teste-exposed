# HexaSilith Chat

Uma aplicação de chat com inteligência artificial desenvolvida em Kotlin que utiliza a API da DeepSeek para conversas interativas via linha de comando.

## ✨ Características

- 🤖 **Integração com IA**: Conversas inteligentes usando a API DeepSeek
- 💾 **Persistência de dados**: Armazenamento local de conversas usando SQLite
- 🎨 **Interface colorida**: Terminal com cores usando ANSI/Jansi
- 📝 **Gestão de conversas**: Criar, listar, carregar e navegar entre conversas
- 🏗️ **Arquitetura limpa**: Separação clara entre camadas (controller, service, repository, model)
- 🗄️ **Migrações automáticas**: Gerenciamento de schema com Flyway

## 🛠️ Tecnologias Utilizadas

- **Kotlin** - Linguagem principal
- **Exposed** - ORM para Kotlin
- **SQLite** - Banco de dados local
- **Flyway** - Gerenciamento de migrações de banco de dados
- **Ktor Client** - Cliente HTTP para requisições à API
- **Jansi** - Cores e formatação no terminal
- **HikariCP** - Pool de conexões de banco de dados
- **Typesafe Config** - Gerenciamento de configurações
- **Kotlinx Serialization** - Serialização JSON

## 📋 Pré-requisitos

- Java 11 ou superior
- Kotlin 1.9+
- Chave de API da DeepSeek

## ⚙️ Configuração

1. **Clone o repositório**
   ```bash
   git clone <url-do-repositorio>
   cd teste-exposed
   ```

2. **Configure a chave da API**
   
   Crie um arquivo `application.conf` em `src/main/resources/`:
   ```hocon
   api {
     key = "sua-chave-api-deepseek"
   }
   
   database {
     path = "./data/chat.db"
   }
   ```

3. **Configure o logging (opcional)**
   
   O arquivo `simplelogger.properties` já está configurado em `src/main/resources/`

## 🚀 Execução

### Via IDE
Execute a função `main` no arquivo `Main.kt`

### Via linha de comando
```bash
./gradlew run
```

**Nota**: Na primeira execução, o Flyway executará automaticamente as migrações necessárias para criar o schema do banco de dados.

## 📖 Como Usar

Ao iniciar a aplicação, você verá o banner do HexaSilith Chat e as opções disponíveis:

### Comandos Disponíveis

- **Enviar mensagem**: Digite diretamente sua mensagem e pressione Enter
- `/new` - Criar uma nova conversa
- `/list` - Listar todas as conversas salvas
- `/load <id>` - Carregar uma conversa específica pelo ID
- `/delete <id>` - Excluir uma conversa (funcionalidade em desenvolvimento)
- `/exit` - Sair da aplicação

### Exemplo de Uso

```
> Olá, como você pode me ajudar?
AI: Olá! Eu posso ajudá-lo com diversas tarefas...

> /list
=== Suas Conversas ===
123e4567-e89b-12d3-a456-426614174000 - Olá, como você pode me ajudar? (2024-01-15 14:30:22)

> /load 123e4567-e89b-12d3-a456-426614174000
=== Olá, como você pode me ajudar? ===
You: Olá, como você pode me ajudar?
AI: Olá! Eu posso ajudá-lo com diversas tarefas...
```

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas bem definida:

```
src/main/kotlin/
├── config/           # Configurações da aplicação
│   ├── AppConfig.kt
│   └── DatabaseConfig.kt (com integração Flyway)
├── controller/       # Controladores da aplicação
│   └── ChatController.kt
├── model/           # Modelos de dados e tabelas
│   ├── Conversation.kt
│   ├── Conversations.kt
│   ├── Message.kt
│   ├── Messages.kt
│   ├── Role.kt
│   ├── Roles.kt
│   ├── ApiRawResponse.kt
│   └── ApiRawResponses.kt
├── repository/      # Camada de acesso a dados
│   ├── ConversationRepository.kt
│   ├── MessageRepository.kt
│   └── ApiRawResponseRepository.kt
├── service/         # Lógica de negócio
│   ├── ConversationService.kt
│   └── AIService.kt
├── util/            # Utilitários
│   ├── ConsolePrinter.kt
│   └── InputReader.kt
└── Main.kt          # Ponto de entrada da aplicação

src/main/resources/
└── db/migration/    # Scripts de migração Flyway
    └── V1__Create_roles_table.sql
```

### Principais Componentes

- **ChatController**: Gerencia a interação do usuário e coordena as operações
- **ConversationService**: Lógica de negócio para conversas e mensagens
- **AIService**: Integração com a API da DeepSeek
- **Repositories**: Acesso aos dados (conversas, mensagens, respostas brutas da API)
- **ConsolePrinter**: Formatação e exibição colorida no terminal
- **DatabaseConfig**: Configuração do banco SQLite com HikariCP e Flyway

## 🗃️ Modelo de Dados

### Tabelas Principais

- **conversations**: Armazena informações das conversas
- **messages**: Armazena mensagens individuais com roles (USER, ASSISTANT, SYSTEM)
- **roles**: Tipos de participantes nas conversas (gerenciada pelo Flyway)
- **api_raw_responses**: Log das respostas brutas da API (para debug)

## 🔄 Migrações de Banco de Dados

O projeto utiliza **Flyway** para gerenciamento de migrações:

### Estrutura de Migrações
- **V1__Create_roles_table.sql**: Cria tabela de roles e popula com dados iniciais
- Futuras migrações seguem o padrão `V{número}__{descrição}.sql`

### Como Funciona
1. Na primeira execução, o Flyway cria a tabela `flyway_schema_history`
2. Executa todas as migrações pendentes na ordem correta
3. Registra cada migração executada para evitar re-execução
4. O Exposed então cria as tabelas restantes (conversations, messages, api_raw_responses)

### Adicionando Nova Migração
1. Crie um arquivo em `src/main/resources/db/migration/`
2. Use o padrão de nomenclatura: `V{número}__{descrição}.sql`
3. A migração será executada automaticamente na próxima inicialização

## 🔧 Configurações Avançadas

### Timeout da API
O cliente HTTP está configurado com timeouts generosos para a API da DeepSeek:
- Request timeout: 10 minutos
- Socket timeout: 10 minutos  
- Connect timeout: 5 minutos

### Pool de Conexões
O HikariCP está configurado com:
- Pool máximo: 1 conexão (adequado para SQLite)
- Auto-commit: desabilitado

### Flyway
- Localização das migrações: `classpath:db/migration`
- Execução automática na inicialização da aplicação
- Compatível com SQLite

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Contribuindo com Migrações
- Sempre crie uma nova migração para mudanças de schema
- Nunca modifique migrações já executadas
- Teste as migrações localmente antes do commit

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 🔍 Funcionalidades Futuras

- [ ] Implementar exclusão de conversas
- [ ] Adicionar suporte a diferentes modelos de IA
- [ ] Implementar exportação de conversas
- [ ] Adicionar sistema de tags para conversas
- [ ] Interface web opcional
- [ ] Suporte a arquivos e imagens
- [ ] Migrações para índices de performance
- [ ] Backup automático do banco de dados

## 🐛 Problemas Conhecidos

- A funcionalidade de exclusão de conversas ainda não está implementada
- O sistema de geração automática de títulos usa apenas os primeiros 50 caracteres da mensagem

## 📞 Suporte

Para reportar bugs ou solicitar funcionalidades, abra uma issue no repositório do projeto.

## 🔧 Troubleshooting

### Problemas de Migração
Se houver problemas com migrações:
1. Verifique se o arquivo SQLite não está corrompido
2. Delete o banco e execute novamente (dados serão perdidos)
3. Verifique os logs do Flyway para detalhes do erro

### Performance
Para melhor performance com grandes volumes de dados:
- Considere adicionar índices via migrações futuras
- Monitor o tamanho do arquivo SQLite