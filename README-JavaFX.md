# Interface Gráfica JavaFX - DeepSeek AI Chat Client

## Visão Geral

Esta é a implementação da interface gráfica do cliente de chat AI usando JavaFX, conforme especificado na Fase 1 das instruções. A interface permite interação com o serviço DeepSeek AI através de uma interface moderna e intuitiva.

## Funcionalidades Implementadas

### 1. **Interface Principal**
- **Barra lateral esquerda**: Lista de conversas existentes
- **Área central**: Visualização de mensagens e campo de entrada
- **Botão "Nova Conversa"**: Criar novas conversas
- **Campo de texto**: Entrada de mensagens com suporte a quebras de linha

### 2. **Gestão de Conversas**
- Listagem de todas as conversas do banco de dados
- Seleção de conversas para visualização
- Criação de novas conversas
- Integração completa com o sistema de persistência

### 3. **Sistema de Mensagens**
- Exibição de mensagens do usuário e da IA com estilos distintos
- Timestamp das mensagens
- Scroll automático para mensagens mais recentes
- Suporte a texto longo com quebra de linha

### 4. **Integração com Serviços**
- Conexão direta com `ConversationService`
- Persistência automática no banco de dados
- Chamadas assíncronas para a API DeepSeek
- Tratamento de erros com alertas visuais

## Estrutura de Arquivos

```
src/main/kotlin/presentation/
├── JavaFXApp.kt                    # Classe principal da aplicação JavaFX
├── MainJavaFX.kt                   # Ponto de entrada alternativo
├── controller/
│   ├── MainController.kt           # Controller com dados mock (para testes)
│   └── IntegratedMainController.kt # Controller integrado com serviços reais
├── model/
│   ├── ChatMessage.kt              # Modelo de mensagem para UI
│   └── ConversationItem.kt         # Modelo de conversa para UI
└── service/
    └── MockChatService.kt          # Serviço mock para testes

src/main/resources/
├── fxml/
│   └── main-view.fxml              # Layout da interface principal
└── css/
    └── main-style.css              # Estilos da interface
```

## Como Executar

### Opção 1: Interface Gráfica (Padrão)
```bash
./gradlew run
```
ou
```bash
./gradlew run --args="--gui"
```

### Opção 2: Interface de Console
```bash
./gradlew run --args="--console"
```

## Características Técnicas

### 1. **Arquitetura**
- **MVC Pattern**: Separação clara entre Model, View e Controller
- **Injeção de Dependências**: Controllers recebem serviços via construtor
- **Programação Assíncrona**: Uso de Kotlin Coroutines para operações não-bloqueantes

### 2. **Tecnologias Utilizadas**
- **JavaFX 23**: Framework de interface gráfica
- **FXML**: Definição declarativa de layouts
- **CSS**: Estilização moderna da interface
- **Kotlin Coroutines**: Programação assíncrona e reativa

### 3. **Responsividade**
- Layout responsivo que se adapta ao redimensionamento da janela
- Tamanho mínimo da janela: 800x600 pixels
- Tamanho inicial: 1200x800 pixels

## Estilos e Design

### Paleta de Cores
- **Sidebar**: Tons de azul escuro (#2c3e50, #34495e)
- **Mensagens do usuário**: Azul (#3498db)
- **Mensagens da IA**: Cinza claro (#ecf0f1)
- **Acentos**: Azul hover (#2980b9)

### Fontes
- **Família principal**: Segoe UI, Arial, sans-serif
- **Tamanho base**: 14px
- **Títulos**: 16-18px com peso bold

## Funcionalidades Futuras

As próximas fases incluirão:
- Histórico de conversas com busca
- Configurações de usuário
- Temas personalizáveis
- Exportação de conversas
- Notificações em tempo real

## Integração com Sistema Existente

A interface JavaFX se integra perfeitamente com:
- **DatabaseConfig**: Configuração do banco de dados
- **ConversationService**: Lógica de negócio de conversas
- **AIService**: Comunicação com a API DeepSeek
- **Repositórios**: Persistência de dados

## Tratamento de Erros

- Alertas visuais para erros de conexão
- Mensagens de erro descritivas
- Recuperação automática de estado
- Logs detalhados para debugging

## Testes

Para executar os testes da interface:
```bash
./gradlew test
```

Os testes incluem:
- Testes unitários dos controllers
- Testes de integração com serviços
- Testes de UI com TestFX (planejado para próximas fases)
