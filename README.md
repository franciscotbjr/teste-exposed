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

## 📊 Diagramas de Sequência - Fluxos Implementados

Esta seção documenta todos os fluxos de funcionamento implementados no HexaSilith Chat através de diagramas de sequência detalhados.

### 💬 Fluxo Principal de Chat

#### 1. Envio de Mensagem do Usuário
```mermaid
sequenceDiagram
    participant U as Usuário
    participant IC as IntegratedMainController
    participant CS as ConversationService
    participant MR as MessageRepository
    participant AS as AIService
    participant API as DeepSeek API
    participant RR as ApiRawResponseRepository
    
    U->>IC: Digite mensagem + Enter
    IC->>IC: Validar entrada não vazia
    IC->>IC: Calcular tokens estimados
    IC->>IC: Atualizar contador de tokens
    IC->>IC: Criar mensagem do usuário na UI
    
    IC->>CS: sendMessage(conversationId, userMessage)
    CS->>MR: create(conversationId, USER, userMessage)
    CS->>MR: findByConversationId(conversationId)
    MR-->>CS: List<Message> history
    
    CS->>AS: chatCompletion(history)
    AS->>AS: Formatar mensagens para API
    AS->>API: POST /v1/chat/completions
    API-->>AS: JSON response
    AS->>AS: Parse da resposta JSON
    AS-->>CS: Pair(aiResponse, rawResponse)
    
    CS->>RR: create(conversationId, rawResponse)
    CS->>MR: create(conversationId, ASSISTANT, aiResponse)
    CS-->>IC: aiResponse
    
    IC->>IC: Calcular tokens da resposta IA
    IC->>IC: Atualizar contador total
    IC->>IC: Criar mensagem da IA na UI
    IC->>IC: Scroll automático para final
```

#### 2. Criação e Carregamento de Conversas
```mermaid
sequenceDiagram
    participant U as Usuário
    participant IC as IntegratedMainController
    participant CS as ConversationService
    participant CR as ConversationRepository
    participant MR as MessageRepository
    participant DC as DataConverter
    
    Note over U,DC: Criação de Nova Conversa
    U->>IC: Clica "Nova Conversa"
    IC->>CS: createConversation("Nova conversa")
    CS->>CS: generateTitleForConversation("Nova conversa")
    CS->>CR: create(title)
    CR-->>CS: Conversation
    CS-->>IC: Conversation
    IC->>DC: toConversationItem(conversation)
    DC-->>IC: ConversationItem
    IC->>IC: Adicionar à lista UI
    IC->>IC: Selecionar nova conversa
    
    Note over U,DC: Carregamento de Conversa Existente
    U->>IC: Seleciona conversa na lista
    IC->>IC: selectConversation(conversationItem)
    IC->>CS: getMessages(UUID.fromString(conversationId))
    CS->>MR: findByConversationId(conversationId)
    MR-->>CS: List<Message>
    CS-->>IC: List<Message>
    IC->>DC: toChatMessages(messages)
    DC-->>IC: List<ChatMessage>
    IC->>IC: displayMessages(chatMessages)
    IC->>IC: Calcular tokens totais
    IC->>IC: Atualizar contador na UI
```

### 📝 Fluxo de Sumarização de Conversas

#### 3. Processo Completo de Sumarização
```mermaid
sequenceDiagram
    participant U as Usuário
    participant IC as IntegratedMainController
    participant SMC as SummaryModalController
    participant CS as ConversationService
    participant AS as AIService
    participant API as DeepSeek API
    participant CSR as ConversationSummarizationRepository
    participant RR as ApiRawResponseRepository
    
    Note over U,RR: Fase 1: Iniciação e Confirmação
    U->>IC: Clica botão "📝 Resumir"
    IC->>IC: Verificar conversa selecionada
    IC->>IC: Abrir modal de confirmação
    IC->>IC: Exibir estatísticas (tokens, %)
    U->>IC: Confirma sumarização
    IC->>IC: Fechar modal confirmação
    
    Note over U,RR: Fase 2: Abertura Modal e Inicialização Assíncrona
    IC->>IC: Carregar FXML summary-modal
    IC->>SMC: setModalStage(stage)
    IC->>SMC: setConversationService(service)
    IC->>SMC: setOnNewConversationCallback()
    IC->>IC: modal.show() - Exibe imediatamente
    
    IC->>SMC: startSummarizationAsync(conversationId)
    SMC->>SMC: showProgressState()
    SMC->>SMC: Exibir ProgressIndicator
    SMC->>SMC: Desabilitar botões
    
    Note over U,RR: Fase 3: Processamento Assíncrono
    SMC->>SMC: updateProgress("Coletando mensagens...")
    SMC->>CS: createConversationSummary(conversationId)
    CS->>CS: Validar conversa não vazia
    
    SMC->>SMC: updateProgress("Conectando com API DeepSeek...")
    CS->>AS: summarizeConversation(messages)
    AS->>AS: Formatar conversa para contexto IA
    AS->>AS: Criar prompt especializado em português
    AS->>API: POST /v1/chat/completions (sumarização)
    API-->>AS: JSON response com resumo
    AS->>AS: Parse do resumo
    AS-->>CS: Pair(summary, rawResponse)
    
    SMC->>SMC: updateProgress("Processando resumo...")
    CS->>RR: create(conversationId, rawResponse)
    CS->>CS: calculateTokensForText(summary)
    CS->>CSR: create(originId, summary, tokens, "deepseek")
    CSR-->>CS: ConversationSummarization
    CS-->>SMC: ConversationSummarization
    
    Note over U,RR: Fase 4: Exibição do Resultado
    SMC->>SMC: showSummaryResult(summarization)
    SMC->>SMC: Limpar ProgressIndicator
    SMC->>SMC: Criar MarkdownView
    SMC->>SMC: Renderizar resumo formatado
    SMC->>SMC: Atualizar informações (data, tokens, método)
    SMC->>SMC: Reabilitar botões
    SMC->>SMC: Scroll para topo
    SMC-->>U: Resumo completo visível
```

#### 4. Tratamento de Erros na Sumarização
```mermaid
sequenceDiagram
    participant U as Usuário
    participant SMC as SummaryModalController
    participant CS as ConversationService
    participant AS as AIService
    participant API as DeepSeek API
    
    Note over U,API: Cenário de Erro
    U->>SMC: Inicia sumarização
    SMC->>CS: createConversationSummary(conversationId)
    CS->>AS: summarizeConversation(messages)
    AS->>API: POST /v1/chat/completions
    API-->>AS: HTTP Error / Timeout / API Limit
    AS-->>CS: Exception
    CS-->>SMC: Exception
    
    SMC->>SMC: showError(errorMessage)
    SMC->>SMC: Limpar ProgressIndicator
    SMC->>SMC: Criar interface de erro
    SMC->>SMC: Exibir ícone ⚠️
    SMC->>SMC: Mostrar mensagem do erro
    SMC->>SMC: Criar botão "🔄 Tentar Novamente"
    SMC->>SMC: Desabilitar botões não essenciais
    
    Note over U,API: Retry do Usuário
    U->>SMC: Clica "Tentar Novamente"
    SMC->>SMC: startSummarizationAsync(conversationId)
    Note over SMC: Reinicia processo completo
```

### 🔄 Fluxos de Gerenciamento de Tokens

#### 5. Monitoramento de Tokens em Tempo Real
```mermaid
sequenceDiagram
    participant U as Usuário
    participant IC as IntegratedMainController
    participant TA as TextArea
    participant TL as TokenLabel
    participant Alert as TokenAlert
    
    Note over U,Alert: Digitação com Preview
    U->>TA: Digite caracteres
    TA->>IC: textProperty.addListener()
    IC->>IC: updateInputTokenPreview(newValue)
    IC->>IC: estimateTokens(inputText)
    IC->>IC: totalTokensPreview = current + input
    IC->>TL: text = "current+input/limit"
    
    alt Se exceder limite
        IC->>TL: styleClass.add("token-count-exceeded")
        IC->>TL: Cor vermelha
    else Se próximo do limite (80%)
        IC->>TL: styleClass.add("token-count-warning")  
        IC->>TL: Cor amarela
    else Normal
        IC->>TL: styleClass.add("token-count")
        IC->>TL: Cor normal
    end
    
    Note over U,Alert: Envio de Mensagem
    U->>IC: Envia mensagem
    IC->>IC: Calcular tokens reais
    IC->>IC: currentTokenCount += messageTokens
    IC->>IC: updateTokenCountLabel()
    
    alt Se atingir threshold (80%)
        IC->>Alert: showTokenWarningAlert()
        IC->>Alert: isVisible = true
        IC->>Alert: Exibir porcentagem e botão resumir
    end
```

#### 6. Sistema de Alertas de Token
```mermaid
sequenceDiagram
    participant U as Usuário
    participant IC as IntegratedMainController
    participant Alert as TokenLimitAlert
    participant SMC as SummaryModalController
    
    Note over U,SMC: Ativação do Alerta
    IC->>IC: currentTokenCount >= tokenWarningThreshold
    IC->>Alert: showTokenWarningAlert()
    Alert->>Alert: isVisible = true, isManaged = true
    Alert->>Alert: Calcular porcentagem
    Alert->>Alert: Atualizar mensagem: "Usando X% dos tokens"
    Alert-->>U: Alerta visível na interface
    
    Note over U,SMC: Ação do Usuário no Alerta
    alt Usuário clica "Resumir Agora"
        U->>Alert: Clica alertSummarizeButton
        Alert->>IC: summarizeConversation()
        IC->>SMC: Iniciar fluxo de sumarização
        Note over SMC: Processo completo de sumarização
        IC->>Alert: dismissTokenLimitAlert()
        Alert->>Alert: isVisible = false
    else Usuário clica "Dispensar"
        U->>Alert: Clica dismissAlertButton
        Alert->>IC: dismissTokenLimitAlert()
        Alert->>Alert: isVisible = false
    end
```

### 🎨 Fluxo de Renderização de Markdown

#### 7. Processamento e Exibição de Conteúdo Formatado
```mermaid
sequenceDiagram
    participant SMC as SummaryModalController
    participant MV as MarkdownView
    participant MP as MarkdownParser
    participant Elements as MarkdownElements
    participant UI as JavaFX_UI
    
    Note over SMC,UI: Renderização do Resumo
    SMC->>MV: setMarkdown(summary, isUserMessage=false)
    MV->>MV: this.isUserMessage = false
    MV->>MV: children.clear()
    
    MV->>MP: parseMarkdown(summary)
    MP->>MP: Processar ## Resumo da Conversa
    MP->>MP: Processar ### 📊 Estatísticas
    MP->>MP: Processar ### 🎯 Tópicos Principais
    MP->>MP: Processar ### 💬 Resumo do Conteúdo
    MP->>MP: Processar ### ✨ Pontos-Chave
    MP->>MP: Processar listas e parágrafos
    MP-->>MV: List<MarkdownElement>
    
    loop Para cada elemento
        MV->>Elements: renderElement(element)
        alt Header
            Elements->>Elements: renderHeader() com getTextColor()
            Elements->>Elements: DARKSLATEGRAY para sumarização
        else Paragraph
            Elements->>Elements: renderParagraph() com inline elements
        else UnorderedList/OrderedList
            Elements->>Elements: renderList() com getTextColor()
        else CodeBlock
            Elements->>Elements: renderCodeBlock()
        end
        Elements-->>MV: JavaFX Node
        MV->>UI: children.add(node)
    end
    
    MV->>MV: applyMessageStyle()
    MV->>UI: styleClass.add("ai-markdown-view")
```

### 💾 Fluxos de Persistência e Recuperação

#### 8. Ciclo Completo de Persistência de Sumarização
```mermaid
sequenceDiagram
    participant CS as ConversationService
    participant CSR as ConversationSummarizationRepository
    participant DB as SQLite_Database
    participant Flyway as FlywayMigration
    
    Note over CS,Flyway: Preparação do Schema
    Flyway->>DB: V5__Create_conversation_summarization_table.sql
    DB->>DB: CREATE TABLE conversations_summarizations
    DB->>DB: CREATE INDEXes para performance
    
    Note over CS,Flyway: Criação de Sumarização
    CS->>CSR: create(originId, summary, tokens, method)
    CSR->>CSR: Gerar UUID para id
    CSR->>CSR: timestamp = LocalDateTime.now()
    CSR->>DB: INSERT INTO conversations_summarizations
    DB-->>CSR: Linha inserida
    CSR->>CSR: Mapear ResultRow para ConversationSummarization
    CSR-->>CS: ConversationSummarization
    
    Note over CS,Flyway: Recuperação de Sumarizações
    CS->>CSR: findByOriginConversationId(conversationId)
    CSR->>DB: SELECT * WHERE origin_conversation_id = ?
    CSR->>DB: AND is_active = 1 ORDER BY updated_at
    DB-->>CSR: ResultSet
    CSR->>CSR: Mapear cada linha para ConversationSummarization
    CSR-->>CS: List<ConversationSummarization>
    
    Note over CS,Flyway: Desativação (Soft Delete)
    CS->>CSR: deactivate(summaryId)
    CSR->>DB: UPDATE SET is_active = 0 WHERE id = ?
    DB-->>CSR: Rows affected
    CSR-->>CS: Boolean success
```

#### 9. Carregamento Inicial da Aplicação
```mermaid
sequenceDiagram
    participant Main as Main.kt
    participant JavaFXApp as JavaFXApp
    participant Config as AppConfig
    participant DB as DatabaseConfig
    participant Flyway as Flyway
    participant IC as IntegratedMainController
    participant Services as Services
    
    Note over Main,Services: Inicialização da Aplicação
    Main->>JavaFXApp: launch()
    JavaFXApp->>Config: loadConfiguration()
    Config->>Config: Carregar application.conf
    Config-->>JavaFXApp: Configurações
    
    JavaFXApp->>DB: configureDatabaseAndRunMigrations()
    DB->>DB: Configurar HikariCP
    DB->>Flyway: Flyway.configure().load()
    Flyway->>Flyway: migrate() - V1 a V5
    Flyway-->>DB: Schema atualizado
    DB-->>JavaFXApp: Database pronto
    
    JavaFXApp->>Services: Criar instâncias dos Services
    Services->>Services: ConversationService(repositories...)
    Services->>Services: AIService(httpClient, apiKey)
    Services-->>JavaFXApp: Services configurados
    
    JavaFXApp->>IC: IntegratedMainController(conversationService)
    JavaFXApp->>JavaFXApp: Carregar FXML main-view
    JavaFXApp->>IC: initialize()
    IC->>IC: setupConversationList()
    IC->>IC: setupMessageInput() 
    IC->>IC: setupButtons()
    IC->>IC: setupSummarizationFeatures()
    IC->>IC: loadConversations() - Async
    IC-->>JavaFXApp: Interface pronta
    
    JavaFXApp-->>Main: Aplicação iniciada
```

### 🔄 Fluxo de Nova Conversa a partir de Resumo

#### 10. Criação de Conversa Baseada em Sumarização
```mermaid
sequenceDiagram
    participant U as Usuário
    participant SMC as SummaryModalController
    participant IC as IntegratedMainController
    participant CI as ConversationItem
    participant CM as ChatMessage
    participant UI as Interface
    
    Note over U,UI: Processo de Nova Conversa
    U->>SMC: Clica "🆕 Nova Conversa"
    SMC->>SMC: showNewConversationConfirmation()
    SMC->>SMC: Abrir modal de confirmação
    SMC->>SMC: Exibir informações sobre nova conversa
    U->>SMC: Confirma criação
    SMC->>SMC: Fechar confirmação
    SMC->>SMC: createNewConversation()
    SMC->>IC: onNewConversationCallback.invoke()
    
    IC->>IC: createNewConversationFromSummary()
    IC->>IC: Verificar currentSummary disponível
    IC->>CI: Criar ConversationItem mockado
    CI->>CI: id = UUID.randomUUID()
    CI->>CI: title = "Nova conversa baseada em resumo"
    CI->>CI: lastMessageTime = now()
    
    IC->>IC: conversations.add(0, newConversationItem)
    IC->>IC: conversationList.selectionModel.select(0)
    IC->>IC: messagesContainer.children.clear()
    
    IC->>CM: Criar ChatMessage inicial
    CM->>CM: content = "**Contexto da conversa anterior:**\n\n$summary\n\n---\n\n*Nova conversa iniciada...*"
    CM->>CM: isUser = false
    CM->>CM: timestamp = now()
    
    IC->>IC: createMessageBox(initialMessage)
    IC->>UI: messagesContainer.children.add(messageBox)
    IC->>IC: Calcular tokens da mensagem inicial
    IC->>IC: updateTokenCountLabel()
    IC->>IC: currentSummary = null
    
    SMC->>SMC: closeModal()
    IC->>IC: showInfoMessage("Nova conversa criada com base no resumo!")
```

---

**💡 Observações sobre os Diagramas:**

1. **Operações Assíncronas**: Fluxos marcados com "Async" são executados em background threads
2. **Tratamento de Erros**: Cada fluxo principal possui tratamento de exceções correspondente  
3. **Persistência**: Todas as operações de banco utilizam transações automáticas do Exposed
4. **UI Threading**: Atualizações de interface sempre executadas no JavaFX Application Thread
5. **Token Management**: Cálculos executados localmente para performance, validação na API

Estes diagramas documentam o estado atual da implementação após conclusão do **Passo 6 da Fase 4**, incluindo a integração real com a API DeepSeek e o sistema completo de sumarização assíncrona.

## 🧪 Testes
