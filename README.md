# HexaSilith Chat

Uma aplicação de chat com inteligência artificial desenvolvida em Kotlin que utiliza a API da DeepSeek para conversas interativas via linha de comando.

## ✨ Características

- 🤖 **Integração com IA**: Conversas inteligentes usando a API DeepSeek
- 💾 **Persistência de dados**: Armazenamento local de conversas usando SQLite
- 🎨 **Interface colorida**: Terminal com cores usando ANSI/Jansi
- 📝 **Gestão de conversas**: Criar, listar, carregar e navegar entre conversas
- 🏗️ **Arquitetura limpa**: Separação clara entre camadas (controller, service, repository, model)

## 🛠️ Tecnologias Utilizadas

- **Kotlin** - Linguagem principal
- **Exposed** - ORM para Kotlin
- **SQLite** - Banco de dados local
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
│   └── DatabaseConfig.kt
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
```

### Principais Componentes

- **ChatController**: Gerencia a interação do usuário e coordena as operações
- **ConversationService**: Lógica de negócio para conversas e mensagens
- **AIService**: Integração com a API da DeepSeek
- **Repositories**: Acesso aos dados (conversas, mensagens, respostas brutas da API)
- **ConsolePrinter**: Formatação e exibição colorida no terminal
- **DatabaseConfig**: Configuração do banco SQLite com HikariCP

## 🗃️ Modelo de Dados

### Tabelas Principais

- **conversations**: Armazena informações das conversas
- **messages**: Armazena mensagens individuais com roles (USER, ASSISTANT, SYSTEM)
- **roles**: Tipos de participantes nas conversas
- **api_raw_responses**: Log das respostas brutas da API (para debug)

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

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 🔍 Funcionalidades Futuras

- [ ] Implementar exclusão de conversas
- [ ] Adicionar suporte a diferentes modelos de IA
- [ ] Implementar exportação de conversas
- [ ] Adicionar sistema de tags para conversas
- [ ] Interface web opcional
- [ ] Suporte a arquivos e imagens

## 🐛 Problemas Conhecidos

- A funcionalidade de exclusão de conversas ainda não está implementada
- O sistema de geração automática de títulos usa apenas os primeiros 50 caracteres da mensagem

## 📞 Suporte

Para reportar bugs ou solicitar funcionalidades, abra uma issue no repositório do projeto.