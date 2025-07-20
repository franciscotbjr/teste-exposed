# HexaSilith Chat

Uma aplicação de chat com inteligência artificial desenvolvida em Kotlin que utiliza a API da DeepSeek para conversas interativas. O projeto oferece tanto interface de linha de comando (CLI) quanto interface gráfica (GUI) com JavaFX, incluindo funcionalidades avançadas de sumarização de conversas.

## ✨ Características

- 🤖 **Integração com IA**: Conversas inteligentes usando a API DeepSeek
- 🖥️ **Interface Dupla**: CLI e GUI (JavaFX) com experiências completas
- 📝 **Sumarização de Conversas**: Geração automática de resumos com persistência
- 🎯 **Gerenciamento de Tokens**: Contagem em tempo real e alertas de limite
- 💾 **Persistência Avançada**: Armazenamento completo incluindo sumarizações
- 🎨 **Interface Moderna**: JavaFX com suporte a Markdown e design responsivo
- 📊 **Histórico Completo**: Visualização e navegação entre conversas
- 🏗️ **Arquitetura Limpa**: Separação em camadas (Controller → Service → Repository)
- 🗄️ **Migrações Automáticas**: Gerenciamento completo de schema com Flyway

## 🛠️ Tecnologias Utilizadas

### Backend e Core
- **Kotlin** - Linguagem principal
- **Exposed** - ORM para Kotlin
- **SQLite** - Banco de dados local
- **Flyway** - Gerenciamento de migrações
- **HikariCP** - Pool de conexões

### Interface e Comunicação
- **JavaFX** - Interface gráfica moderna
- **Ktor Client** - Cliente HTTP para API
- **Kotlinx Serialization** - Serialização JSON
- **Jansi** - Cores no terminal (CLI)

### Configuração e Utilitários
- **Typesafe Config** - Gerenciamento de configurações
- **JUnit 5** - Framework de testes
- **Markdown Parser** - Renderização de texto formatado

## 📋 Pré-requisitos

- Java 21 ou superior
- Kotlin 2.1+
- Chave de API da DeepSeek
- JavaFX (incluído nas dependências)

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

### Interface Gráfica (GUI) - Padrão
```bash
./gradlew run
```

### Interface de Linha de Comando (CLI)
```bash
./gradlew run --args="cli"
```

### Via IDE
Execute a função `main` no arquivo `Main.kt`

**Nota**: Na primeira execução, o Flyway executará automaticamente as migrações necessárias para criar o schema completo do banco de dados.

## 📖 Como Usar

### 🖥️ Interface Gráfica (JavaFX)

A interface gráfica oferece uma experiência moderna e intuitiva:

#### Funcionalidades Principais:
- **📝 Chat Interativo**: Envio e recebimento de mensagens com a IA
- **📋 Lista de Conversas**: Navegação entre conversas salvas
- **🔄 Sumarização**: Geração de resumos das conversas atuais
- **📊 Contagem de Tokens**: Monitoramento em tempo real do uso de tokens
- **🎨 Suporte a Markdown**: Renderização completa de texto formatado
- **💾 Histórico Completo**: Visualização de todas as mensagens e resumos

#### Interface de Sumarização:
1. **Botão "Resumir"**: Disponível na interface principal
2. **Modal de Confirmação**: Confirma antes de gerar o resumo
3. **Processamento**: Indicador visual durante a geração
4. **Exibição do Resultado**: Modal dedicado com:
   - Resumo formatado em Markdown
   - Informações de tokens utilizados
   - Método de sumarização usado
   - Opções para copiar ou criar nova conversa

#### Gerenciamento de Tokens:
- **Contagem em Tempo Real**: Exibida durante a digitação
- **Sistema de Alertas**: Cores visuais indicando proximidade do limite
- **Limite Configurado**: 128k tokens (limite da API DeepSeek)
- **Threshold de Aviso**: Alerta quando atinge 80% do limite

### 💻 Interface de Linha de Comando (CLI)

Ao iniciar a aplicação CLI, você verá o banner do HexaSilith Chat e as opções:

#### Comandos Disponíveis:
- **Enviar mensagem**: Digite diretamente e pressione Enter
- `/new` - Criar uma nova conversa
- `/list` - Listar todas as conversas salvas
- `/load <id>` - Carregar uma conversa específica pelo ID
- `/delete <id>` - Excluir uma conversa
- `/exit` - Sair da aplicação

#### Exemplo de Uso:
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

O projeto segue uma arquitetura em camadas bem definida com separação clara de responsabilidades:

```
src/main/kotlin/
├── config/                    # Configurações da aplicação
│   ├── AppConfig.kt
│   └── DatabaseConfig.kt     # Integração Flyway + HikariCP
├── controller/               # Controladores da aplicação
│   └── ChatController.kt     # CLI Controller
├── presentation/             # Camada de apresentação JavaFX
│   ├── JavaFXApp.kt         # Aplicação principal JavaFX
│   ├── controller/          # Controllers JavaFX
│   │   ├── IntegratedMainController.kt
│   │   ├── SummaryModalController.kt
│   │   └── SummaryConfirmationController.kt
│   ├── component/           # Componentes reutilizáveis
│   │   ├── MarkdownParser.kt
│   │   └── MarkdownView.kt
│   └── service/             # Serviços da apresentação
│       └── MockChatService.kt
├── model/                   # Modelos de dados e tabelas Exposed
│   ├── Conversation.kt / Conversations.kt
│   ├── Message.kt / Messages.kt
│   ├── Role.kt / Roles.kt
│   ├── ApiRawResponse.kt / ApiRawResponses.kt
│   └── ConversationSummarization.kt / ConversationsSummarizations.kt
├── repository/              # Camada de acesso a dados
│   ├── ConversationRepository.kt
│   ├── MessageRepository.kt
│   ├── ApiRawResponseRepository.kt
│   └── ConversationSummarizationRepository.kt  # ✅ NOVO
├── service/                 # Lógica de negócio
│   ├── ConversationService.kt  # Serviço principal
│   └── AIService.kt           # Integração com API
├── util/                    # Utilitários
│   ├── ConsolePrinter.kt
│   └── InputReader.kt
└── Main.kt                  # Ponto de entrada

src/main/resources/
├── db/migration/            # Scripts Flyway
│   ├── V1__Create_roles_table.sql
│   ├── V2__Create_conversations_table.sql
│   ├── V3__Create_messages_table.sql
│   ├── V4__Create_api_raw_responses_table.sql
│   └── V5__Create_conversation_summarization_table.sql  # ✅ NOVO
├── fxml/                    # Interfaces JavaFX
└── css/                     # Estilos da interface
```

### Principais Componentes

#### Camada de Apresentação
- **IntegratedMainController**: Interface principal com chat e sumarização
- **SummaryModalController**: Modal para exibição de resumos
- **MarkdownView**: Componente para renderização de Markdown

#### Camada de Serviços
- **ConversationService**: Lógica principal de conversas e sumarização
- **AIService**: Integração com API DeepSeek
- **MockChatService**: Simulação para desenvolvimento

#### Camada de Dados
- **ConversationSummarizationRepository**: Gestão de resumos ✅ NOVO
- **ConversationRepository**: Gestão de conversas
- **MessageRepository**: Gestão de mensagens
- **ApiRawResponseRepository**: Log de respostas da API

## 🗃️ Modelo de Dados

### Esquema do Banco de Dados

O projeto possui um esquema completo com 5 tabelas principais:

#### Tabelas Principais
- **`roles`**: Tipos de participantes (SYSTEM, USER, ASSISTANT)
- **`conversations`**: Informações das conversas
- **`messages`**: Mensagens individuais das conversas
- **`api_raw_responses`**: Log das respostas brutas da API
- **`conversations_summarizations`**: Sumarizações das conversas ✅ NOVO

#### Nova Funcionalidade: Sumarização
```sql
conversations_summarizations (
    id VARCHAR(36) PRIMARY KEY,
    origin_conversation_id VARCHAR(36) NOT NULL,
    summary TEXT NOT NULL,
    tokens_used INTEGER DEFAULT 0,           -- ✅ Controle de uso
    summary_method VARCHAR(50) DEFAULT 'deepseek', -- ✅ Rastreabilidade  
    is_active BOOLEAN DEFAULT 1,             -- ✅ Soft delete
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (origin_conversation_id) REFERENCES conversations(id)
);
```

#### Relacionamentos
- `conversations` ↔ `messages` (1:N)
- `conversations` ↔ `api_raw_responses` (1:N) 
- `conversations` ↔ `conversations_summarizations` (1:N) ✅ NOVO
- `roles` ↔ `messages` (1:N)

## 🔄 Migrações de Banco de Dados

O projeto utiliza **Flyway** para gerenciamento automatizado de migrações:

### Scripts de Migração Implementados
| Versão | Arquivo | Descrição |
|--------|---------|-----------|
| **V1** | `V1__Create_roles_table.sql` | Cria tabela de roles e dados iniciais |
| **V2** | `V2__Create_conversations_table.sql` | Cria tabela de conversas |
| **V3** | `V3__Create_messages_table.sql` | Cria tabela de mensagens |
| **V4** | `V4__Create_api_raw_responses_table.sql` | Cria log de respostas da API |
| **V5** | `V5__Create_conversation_summarization_table.sql` | ✅ **NOVO**: Sumarizações |

### Processo Automatizado
1. **Primeira Execução**: Flyway cria `flyway_schema_history`
2. **Execução Sequencial**: Todas as migrações pendentes são aplicadas
3. **Controle de Estado**: Cada migração é registrada para evitar re-execução
4. **Criação Exposed**: Tabelas restantes são gerenciadas pelo Exposed ORM

## 🎯 Funcionalidades de Sumarização

### ✅ Implementadas
- **Interface Gráfica**: Modal dedicado para sumarizações
- **Confirmação de Usuário**: Dialog antes de gerar resumo
- **Persistência Completa**: Armazenamento com metadados
- **Contagem de Tokens**: Monitoramento em tempo real
- **Suporte a Markdown**: Renderização formatada dos resumos
- **Gerenciamento de Status**: Ativação/desativação de resumos

### 🔄 Em Desenvolvimento
- **API DeepSeek Real**: Integração com endpoint de sumarização
- **Chamadas Assíncronas**: Otimização de performance
- **Alertas Refinados**: Notificações mais discretas

### ⬜ Planejadas
- **Persistência como Mensagem**: Resumos no histórico de chat
- **Nova Conversa**: Início automático a partir de resumos
- **Exportação**: Salvamento de resumos em múltiplos formatos

## 🔧 Configurações Avançadas

### API DeepSeek
```hocon
api {
  key = "sua-chave-api"
  timeout = 600000      # 10 minutos
  max_tokens = 131072   # 128k tokens
}
```

### Banco de Dados
```hocon
database {
  path = "./data/chat.db"
  pool_size = 1         # Adequado para SQLite
  auto_commit = false
}
```

### Flyway
- **Localização**: `classpath:db/migration`
- **Execução**: Automática na inicialização
- **Compatibilidade**: SQLite otimizado

### Interface JavaFX
- **Tema**: Responsivo com CSS personalizado
- **Renderização**: Markdown nativo com WebView
- **Modais**: Redimensionáveis e centralizados

## 🧪 Testes

### Cobertura Atual
- ✅ **Repository Tests**: Todas as repositories com testes completos
- ✅ **Service Tests**: ConversationService com mocks
- ✅ **Integration Tests**: Flyway migrations e banco de dados
- 🔄 **UI Tests**: Em desenvolvimento para componentes JavaFX

### Executar Testes
```bash
# Todos os testes
./gradlew test

# Testes específicos
./gradlew test --tests "*ConversationSummarizationRepositoryTest*"
./gradlew test --tests "*ConversationServiceTest*"
```

## 📊 Métricas do Projeto

### Linhas de Código (aproximadas)
- **Controllers**: ~1200 LOC (CLI + JavaFX)
- **Services**: ~800 LOC
- **Repositories**: ~600 LOC  
- **Models**: ~400 LOC
- **Tests**: ~1000 LOC
- **Total**: ~4000 LOC

### Funcionalidades por Status
- ✅ **Concluídas**: Interface gráfica, CLI, persistência, sumarização básica
- 🔄 **Em desenvolvimento**: API real, otimizações, refinamentos
- ⬜ **Planejadas**: Exportação, funcionalidades avançadas

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Diretrizes de Contribuição
- **Arquitetura**: Mantenha a separação Controller → Service → Repository
- **Testes**: Adicione testes para novas funcionalidades
- **Migrações**: Sempre crie nova migração para mudanças de schema
- **Documentação**: Atualize README.md e documentação técnica

## 📜 Licença

Este projeto está licenciado sob a [MIT License](LICENSE).

---

## 🏆 Marcos do Projeto

### Fase 1 ✅ - Interface CLI
- Chat interativo via terminal
- Persistência básica
- Comandos de navegação

### Fase 2 ✅ - Interface JavaFX
- GUI moderna e responsiva
- Suporte a Markdown
- Lista de conversas

### Fase 3 ✅ - Melhorias de UX
- Otimizações de interface
- Componentes reutilizáveis
- Estilização avançada

### Fase 4 🔄 - Sumarização (Em Progresso)
- ✅ Interface de sumarização
- ✅ Persistência completa
- ✅ Gerenciamento de tokens
- 🔄 API DeepSeek real
- ⬜ Funcionalidades avançadas

### Fase 5 ⬜ - Funcionalidades Avançadas (Planejada)
- Exportação/importação
- Múltiplos usuários
- Configurações personalizáveis
- Temas e customização

---

**HexaSilith Chat** - Conversas inteligentes com tecnologia moderna 🚀
