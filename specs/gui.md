# Diagrama de Interface Gráfica - HexaSilith Chat

Este documento contém o diagrama de navegação e fluxos de interação da interface gráfica do projeto HexaSilith Chat, incluindo todas as janelas, telas e modais implementados.

## Visão Geral da Arquitetura GUI

O sistema utiliza uma **Single Window Application** com navegação centralizada através do `NavigationController`, suportando múltiplas views dentro de uma única janela principal.

## Diagrama de Navegação Principal

```mermaid
graph TB
    %% Janela Principal
    APP[Janela Principal<br/>ApplicationMainController]
    NAV[NavigationController<br/>Sistema de Navegação]
    
    %% Views Principais
    MAIN[Tela Principal<br/>IntegratedMainController<br/>main-view.fxml]
    SUMM_VIEW[Tela de Sumarizações<br/>SummarizationsController<br/>summarizations-view.fxml]
    
    %% Modais da Tela Principal
    CONF_MODAL[Modal de Confirmação<br/>SummaryConfirmationController<br/>summary-confirmation.fxml]
    SUMM_MODAL[Modal de Resultado<br/>SummaryModalController<br/>summary-modal.fxml]
    
    %% Fluxo Principal
    APP --> NAV
    NAV --> MAIN
    NAV --> SUMM_VIEW
    
    %% Navegação entre telas
    MAIN -.->|"📋 Sumarizações"| SUMM_VIEW
    SUMM_VIEW -.->|"← Voltar"| MAIN
    
    %% Modais da Tela Principal
    MAIN -->|"🔄 Resumir"| CONF_MODAL
    CONF_MODAL -->|"Confirmar"| SUMM_MODAL
    CONF_MODAL -.->|"Cancelar"| MAIN
    SUMM_MODAL -.->|"Fechar"| MAIN
    
    %% Links de Navegação
    SUMM_MODAL -->|"Nova Conversa"| MAIN
    SUMM_VIEW -.->|"Links conversation://"| MAIN
    
    %% Estilos
    classDef window fill:#e1f5fe,stroke:#01579b,stroke-width:3px
    classDef view fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef modal fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef navigation fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    
    class APP window
    class NAV navigation
    class MAIN,SUMM_VIEW view
    class CONF_MODAL,SUMM_MODAL modal
```

## Fluxo Detalhado de Interações

```mermaid
sequenceDiagram
    participant U as Usuário
    participant APP as ApplicationMainController
    participant NAV as NavigationController
    participant MAIN as IntegratedMainController
    participant CONF as SummaryConfirmationController
    participant MODAL as SummaryModalController
    participant SUMM as SummarizationsController
    
    %% Inicialização
    U->>APP: Inicia aplicação
    APP->>NAV: initialize(viewContainer)
    NAV->>MAIN: navigateToMainView()
    MAIN-->>U: Exibe tela principal
    
    %% Fluxo de Sumarização
    U->>MAIN: Clica "🔄 Resumir"
    MAIN->>CONF: Abre modal de confirmação
    U->>CONF: Clica "Confirmar Sumarização"
    CONF->>MODAL: Abre modal de resultado
    MODAL->>MODAL: Executa sumarização (API + BD)
    MODAL-->>U: Exibe resultado formatado
    
    %% Nova Conversa
    U->>MODAL: Clica "Nova Conversa"
    MODAL->>MAIN: Cria nova conversa e fecha modal
    MAIN-->>U: Atualiza lista e seleciona nova conversa
    
    %% Navegação para Sumarizações
    U->>MAIN: Clica "📋 Sumarizações"
    MAIN->>NAV: navigateToSummarizationsView()
    NAV->>SUMM: Carrega tela de sumarizações
    SUMM-->>U: Exibe lista de sumarizações
    
    %% Links de Conversa
    U->>SUMM: Clica link "conversation://"
    SUMM->>NAV: navigateToConversation(id)
    NAV->>MAIN: Seleciona conversa específica
    MAIN-->>U: Exibe conversa selecionada
```

## Estrutura de Arquivos GUI

```mermaid
graph LR
    subgraph "Controllers"
        AC[ApplicationMainController]
        IMC[IntegratedMainController]
        SC[SummarizationsController]
        SMC[SummaryModalController]
        SCC[SummaryConfirmationController]
        NC[NavigationController]
    end
    
    subgraph "FXML Views"
        AM[application-main.fxml]
        MV[main-view.fxml]
        SV[summarizations-view.fxml]
        SM[summary-modal.fxml]
        SCF[summary-confirmation.fxml]
    end
    
    subgraph "CSS Styles"
        MS[main-style.css]
    end
    
    %% Relacionamentos
    AC -.-> AM
    IMC -.-> MV
    SC -.-> SV
    SMC -.-> SM
    SCC -.-> SCF
    
    MV -.-> MS
    SV -.-> MS
    SM -.-> MS
    SCF -.-> MS
    
    classDef controller fill:#bbdefb,stroke:#1976d2
    classDef fxml fill:#c8e6c9,stroke:#388e3c
    classDef css fill:#ffecb3,stroke:#f57c00
    
    class AC,IMC,SC,SMC,SCC,NC controller
    class AM,MV,SV,SM,SCF fxml
    class MS css
```

## Estados das Telas

### Tela Principal (IntegratedMainController)

```mermaid
stateDiagram-v2
    [*] --> Initial
    Initial --> ConversationSelected : Seleciona conversa
    Initial --> Empty : Sem conversas
    ConversationSelected --> Summarizing : Clica "Resumir"
    Summarizing --> ConversationSelected : Sumarização completa
    ConversationSelected --> SummarizationsView : Clica "Sumarizações"
    Empty --> ConversationSelected : Cria nova conversa
    SummarizationsView --> ConversationSelected : Volta da tela
```

### Tela de Sumarizações (SummarizationsController)

```mermaid
stateDiagram-v2
    [*] --> Welcome
    Welcome --> ItemSelected : Seleciona sumarização
    Welcome --> Empty : Sem sumarizações
    ItemSelected --> Welcome : Deseleciona item
    ItemSelected --> MainView : Clica link conversa
    Empty --> MainView : Volta para criar sumarização
```

## Componentes de Interface por Tela

### 🏠 Tela Principal (main-view.fxml)
- **Sidebar Esquerda**: Lista de conversas
- **Área Central**: Chat com mensagens
- **Campo de Input**: Entrada de texto + botão enviar
- **Barra Superior**: Título da conversa + contagem de tokens
- **Botões de Ação**: 
  - 🔄 Resumir Conversa
  - 📋 Ver Sumarizações

### 📋 Tela de Sumarizações (summarizations-view.fxml)
- **Sidebar Esquerda**: Lista de sumarizações
- **Área Central**: Conteúdo da sumarização selecionada
- **Header**: Botão "← Voltar" + informações da seleção
- **Estados**:
  - Boas-vindas (sem seleção)
  - Vazio (sem sumarizações)
  - Conteúdo (sumarização selecionada)

### 🔍 Modal de Confirmação (summary-confirmation.fxml)
- **Título**: "Confirmar Sumarização"
- **Mensagem**: Explicação da operação
- **Botões**: "Confirmar Sumarização" | "Cancelar"

### 📊 Modal de Resultado (summary-modal.fxml)
- **Header**: Informações da conversa e tokens
- **Área de Conteúdo**: Sumarização formatada (Markdown)
- **Botões de Ação**:
  - 📋 Copiar
  - 💾 Exportar
  - 🆕 Nova Conversa
  - ❌ Fechar

## Navegação e Links Especiais

### Protocolo conversation://
- **Formato**: `conversation://uuid`
- **Função**: Links clicáveis para navegar entre conversas
- **Localização**: Mensagens de sumarização e tela de sumarizações
- **Comportamento**: Fecha tela atual e navega para conversa específica

### Callbacks de Navegação
```kotlin
// IntegratedMainController
onNavigateToSummarizations: () -> Unit

// SummarizationsController  
onBackToMainScreen: () -> Unit
onConversationLinkClick: (String) -> Unit

// SummaryModalController
onClose: () -> Unit
onNewConversationCreated: (Conversation) -> Unit
```

## Responsividade e Estados Visuais

### Contagem de Tokens
- **Verde**: 0-60% do limite (≤76.8k tokens)
- **Amarelo**: 60-80% do limite (76.8k-102.4k tokens)  
- **Vermelho**: 80-100% do limite (102.4k-128k tokens)

### Lista de Conversas
- **Item Selecionado**: Destacado em azul
- **Hover**: Efeito de destaque
- **Última Atividade**: Ordenação por `updated_at`

### Lista de Sumarizações
- **Tooltips**: Informações detalhadas no hover
- **Formatação**: Data, tokens, método e preview
- **Estados**: Ativo/Inativo com indicadores visuais

## Integração com Backend

### Chamadas Assíncronas
- **Sumarização**: API DeepSeek com feedback de progresso
- **Carregamento**: Listas e conteúdo via coroutines
- **Persistência**: SQLite com transações otimizadas

### Gerenciamento de Estado
- **Observables**: JavaFX ObservableList para listas dinâmicas
- **Atualizações**: Platform.runLater() para UI thread
- **Cache**: Reutilização de controllers para performance

---

## 📊 Estatísticas da Interface

### Telas Implementadas
- **1** Janela Principal (ApplicationMainController)
- **2** Views Principais (Main + Sumarizações) 
- **2** Modais (Confirmação + Resultado)
- **1** Sistema de Navegação (NavigationController)

### Arquivos de Interface
- **5** Arquivos FXML
- **6** Controllers
- **1** Arquivo CSS principal
- **1** Sistema de navegação centralizado

### Funcionalidades de Navegação
- ✅ Navegação entre telas na mesma janela
- ✅ Links internos com protocolo personalizado
- ✅ Modais de confirmação e resultado
- ✅ Estados dinâmicos (vazio, carregando, conteúdo)
- ✅ Callbacks para interação entre components

---

*Diagrama criado com base no estado atual da implementação da Fase 4 - Passo 8*

**Última atualização**: Janeiro 2025 - Sistema de navegação com tela única implementado