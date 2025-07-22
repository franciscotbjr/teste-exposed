# Guia de Execução - Interface JavaFX

## Como Executar a Interface Gráfica

### 1. Compilar o Projeto
```bash
./gradlew build
```

### 2. Executar a Interface Gráfica (Padrão)
```bash
./gradlew run
```

### 3. Executar com Interface de Console
```bash
./gradlew run --args="--console"
```

### 4. Executar com Interface Gráfica Explícita
```bash
./gradlew run --args="--gui"
```

## Verificações de Funcionamento

### 1. **Inicialização**
- O banco de dados deve ser inicializado automaticamente
- A interface deve abrir uma janela de 1200x800 pixels
- A sidebar deve mostrar "Conversas" e o botão "+"

### 2. **Funcionalidades Básicas**
- Criar nova conversa clicando no botão "+"
- Selecionar conversas na lista lateral
- Digitar mensagens na área de texto
- Enviar mensagens com Enter ou botão "Enviar"

### 3. **Integração com Serviços**
- Mensagens devem ser persistidas no banco
- Respostas da IA devem aparecer automaticamente
- Conversas devem ser carregadas do banco de dados

## Estrutura de Testes

### Testes Unitários
```bash
./gradlew test
```

### Testes de Interface (Manual)
1. **Teste de Criação de Conversa**
   - Clicar no botão "+"
   - Verificar se nova conversa aparece na lista
   - Verificar se a conversa é selecionada automaticamente

2. **Teste de Envio de Mensagem**
   - Digitar mensagem na área de texto
   - Pressionar Enter ou clicar "Enviar"
   - Verificar se mensagem aparece na área de chat
   - Verificar se resposta da IA aparece após alguns segundos

3. **Teste de Navegação**
   - Criar múltiplas conversas
   - Clicar em diferentes conversas na lista
   - Verificar se as mensagens corretas são exibidas

## Logs e Avisos Conhecidos

### Aviso JavaFX Módulos (Pode ser ignorado)
```
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @27462a88'
```
Este aviso é comum em projetos que não usam o sistema de módulos do Java (JPMS) e pode ser ignorado com segurança. A aplicação funciona perfeitamente.

### Logs de Execução Normal
```
Inicializando banco de dados e executando migrações...
Migrações executadas com sucesso!
Iniciando interface gráfica JavaFX...
```

## Resolução de Problemas

### Erro "JavaFX runtime components are missing"
```bash
# Instalar JavaFX separadamente ou usar JDK com JavaFX incluído
./gradlew run --args="--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml"
```

### Erro de Conexão com API
- Verificar se o arquivo `application.conf` está configurado
- Verificar se a chave da API DeepSeek está definida
- Verificar conexão com internet

### Erro de Banco de Dados
- Verificar se o arquivo SQLite foi criado
- Verificar permissões de escrita na pasta do projeto
- Verificar se as migrações foram executadas

## Habilitar Logs Detalhados

### Editar `src/main/resources/simplelogger.properties`:
```properties
org.slf4j.simpleLogger.defaultLogLevel=DEBUG
org.slf4j.simpleLogger.log.org.hexasilith=DEBUG
```

### Verificar Logs de Execução
- Logs aparecem no console durante a execução
- Erros de API são mostrados em alertas na interface
- Logs de banco de dados ajudam a debugar problemas de persistência

## Status da Implementação

✅ **Interface gráfica funcionando completamente**  
✅ **Integração com banco de dados SQLite**  
✅ **Comunicação com API DeepSeek**  
✅ **Persistência de conversas e mensagens**  
✅ **Interface responsiva e moderna**  
