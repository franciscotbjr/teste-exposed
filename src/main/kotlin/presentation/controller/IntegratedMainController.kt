package org.hexasilith.presentation.controller

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hexasilith.model.ConversationSummarization
import org.hexasilith.presentation.model.ChatMessage
import org.hexasilith.presentation.model.ConversationItem
import org.hexasilith.presentation.util.DataConverter
import org.hexasilith.presentation.component.MarkdownView
import org.hexasilith.service.ConversationService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class IntegratedMainController(
    private val conversationService: ConversationService
) {

    @FXML
    private lateinit var conversationList: ListView<ConversationItem>

    @FXML
    private lateinit var messageArea: ScrollPane

    @FXML
    private lateinit var messageInput: TextArea

    @FXML
    private lateinit var sendButton: Button

    @FXML
    private lateinit var newConversationButton: Button

    @FXML
    private lateinit var messagesContainer: VBox

    // Elementos de GUI para sumarização
    @FXML
    private lateinit var summarizeButton: Button

    @FXML
    private lateinit var tokenCountLabel: Label

    @FXML
    private lateinit var tokenLimitAlert: HBox

    @FXML
    private lateinit var tokenAlertMessage: Label

    @FXML
    private lateinit var alertSummarizeButton: Button

    @FXML
    private lateinit var dismissAlertButton: Button

    private val conversations: ObservableList<ConversationItem> = FXCollections.observableArrayList()
    private var currentConversation: ConversationItem? = null
    private val coroutineScope = CoroutineScope(Dispatchers.JavaFx)

    // Variáveis para controle de tokens
    private var currentTokenCount: Int = 0
    private val tokenLimit: Int = 128000 // Limite de tokens para DeepSeek (128k)
    private val tokenWarningThreshold: Int = (tokenLimit * 0.8).toInt() // 80% do limite
    private var currentSummary: String? = null

    fun initialize() {
        setupConversationList()
        setupMessageInput()
        setupButtons()
        setupSummarizationFeatures()
        loadConversations()
    }

    private fun setupConversationList() {
        conversationList.items = conversations
        conversationList.setCellFactory { _ ->
            object : ListCell<ConversationItem>() {
                override fun updateItem(item: ConversationItem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {
                        text = item.title
                        tooltip = Tooltip("Última mensagem: ${item.lastMessageTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")
                    }
                }
            }
        }

        conversationList.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { selectConversation(it) }
        }
    }

    private fun setupMessageInput() {
        messageInput.promptText = "Digite sua mensagem aqui..."
        messageInput.isWrapText = true
        messageInput.prefRowCount = 3

        messageInput.setOnKeyPressed { event ->
            if (event.code.toString() == "ENTER" && !event.isShiftDown) {
                event.consume()
                sendMessage()
            }
        }

        // Adicionar listener para atualizar contagem de tokens em tempo real durante a digitação
        messageInput.textProperty().addListener { _, _, newValue ->
            updateInputTokenPreview(newValue)
        }
    }

    private fun updateInputTokenPreview(inputText: String) {
        if (inputText.isNotBlank()) {
            val inputTokens = estimateTokens(inputText)
            val totalTokensPreview = currentTokenCount + inputTokens

            // Atualizar label com preview dos tokens
            val previewText = "$currentTokenCount+$inputTokens/$tokenLimit"
            tokenCountLabel.text = previewText

            // Verificar se excederá o limite
            if (totalTokensPreview >= tokenLimit) {
                tokenCountLabel.styleClass.removeAll("token-count")
                tokenCountLabel.styleClass.add("token-count-exceeded")
            } else if (totalTokensPreview >= tokenWarningThreshold) {
                tokenCountLabel.styleClass.removeAll("token-count", "token-count-exceeded")
                tokenCountLabel.styleClass.add("token-count-warning")
            } else {
                tokenCountLabel.styleClass.removeAll("token-count-warning", "token-count-exceeded")
                tokenCountLabel.styleClass.add("token-count")
            }
        } else {
            // Voltar ao estado normal quando input estiver vazio
            tokenCountLabel.text = "$currentTokenCount/$tokenLimit"
            tokenCountLabel.styleClass.removeAll("token-count-warning", "token-count-exceeded")
            tokenCountLabel.styleClass.add("token-count")
        }
    }

    private fun setupButtons() {
        sendButton.setOnAction { sendMessage() }
        newConversationButton.setOnAction { createNewConversation() }

        messageInput.textProperty().addListener { _, _, newValue ->
            sendButton.isDisable = newValue.isBlank()
        }
        sendButton.isDisable = true
    }

    private fun setupSummarizationFeatures() {
        // Inicializa os elementos de sumarização
        tokenCountLabel.text = "0/$tokenLimit"
        tokenLimitAlert.isVisible = false

        summarizeButton.setOnAction { summarizeConversation() }
        alertSummarizeButton.setOnAction { summarizeConversation() }
        dismissAlertButton.setOnAction { dismissTokenLimitAlert() }
    }

    private fun loadConversations() {
        coroutineScope.launch {
            try {
                val dbConversations = withContext(Dispatchers.IO) {
                    conversationService.listConversations()
                }

                Platform.runLater {
                    conversations.clear()
                    conversations.addAll(DataConverter.toConversationItems(dbConversations))

                    if (conversations.isNotEmpty()) {
                        conversationList.selectionModel.select(0)
                    }
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao carregar conversas: ${e.message}")
                }
            }
        }
    }

    private fun selectConversation(conversation: ConversationItem) {
        currentConversation = conversation
        loadMessages(conversation)
    }

    private fun loadMessages(conversation: ConversationItem) {
        coroutineScope.launch {
            try {
                val messages = withContext(Dispatchers.IO) {
                    conversationService.getMessages(UUID.fromString(conversation.id))
                }

                Platform.runLater {
                    displayMessages(DataConverter.toChatMessages(messages))
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao carregar mensagens: ${e.message}")
                }
            }
        }
    }

    private fun displayMessages(messages: List<ChatMessage>) {
        messagesContainer.children.clear()

        // Resetar contador de tokens ao carregar nova conversa
        currentTokenCount = 0

        messages.forEach { message ->
            val messageBox = createMessageBox(message)
            messagesContainer.children.add(messageBox)

            // Calcular tokens da mensagem carregada
            val messageTokens = estimateTokens(message.content)
            currentTokenCount += messageTokens
        }

        // Atualizar a label de tokens após carregar todas as mensagens
        updateTokenCountLabel()

        Platform.runLater {
            messageArea.vvalue = 1.0
        }
    }

    private fun createMessageBox(message: ChatMessage): HBox {
        val messageRow = HBox()
        messageRow.spacing = 10.0
        messageRow.padding = javafx.geometry.Insets(8.0, 0.0, 8.0, 0.0)

        if (message.isUser) {
            // Mensagem do usuário - estrutura: [spacer] [content] [icon]
            messageRow.styleClass.add("message-row-user")

            // Spacer para empurrar conteúdo para a direita
            val spacer = Region()
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS)

            // Container do conteúdo da mensagem
            val messageContent = VBox()
            messageContent.styleClass.add("message-content-user")
            messageContent.spacing = 4.0

            // MarkdownView para renderizar conteúdo Markdown
            val markdownView = MarkdownView()
            markdownView.setMarkdown(message.content, isUserMessage = true)
            markdownView.prefWidth = 600.0
            markdownView.maxWidth = 600.0

            // Timestamp
            val timeLabel = Label(message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")))
            timeLabel.styleClass.add("user-message-time")

            messageContent.children.addAll(markdownView, timeLabel)

            // Ícone do usuário
            val userIcon = Label("👤")
            userIcon.styleClass.add("user-icon")

            messageRow.children.addAll(spacer, messageContent, userIcon)

        } else {
            // Mensagem da IA - estrutura: [icon] [content] [spacer]
            messageRow.styleClass.add("message-row-ai")

            // Ícone da IA
            val aiIcon = Label("🤖")
            aiIcon.styleClass.add("ai-icon")

            // Container do conteúdo da mensagem
            val messageContent = VBox()
            messageContent.styleClass.add("message-content-ai")
            messageContent.spacing = 4.0

            // MarkdownView para renderizar conteúdo Markdown
            val markdownView = MarkdownView()
            markdownView.setMarkdown(message.content, isUserMessage = false)
            markdownView.prefWidth = 600.0
            markdownView.maxWidth = 600.0

            // Timestamp
            val timeLabel = Label(message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")))
            timeLabel.styleClass.add("ai-message-time")

            messageContent.children.addAll(markdownView, timeLabel)

            // Spacer para empurrar conteúdo para a esquerda
            val spacer = Region()
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS)

            messageRow.children.addAll(aiIcon, messageContent, spacer)
        }

        return messageRow
    }

    private fun sendMessage() {
        val text = messageInput.text.trim()
        if (text.isBlank() || currentConversation == null) return

        // Calcular tokens antes de enviar a mensagem
        val estimatedTokens = estimateTokens(text)
        currentTokenCount += estimatedTokens
        updateTokenCountLabel()

        val userMessage = ChatMessage(
            content = text,
            isUser = true,
            timestamp = LocalDateTime.now()
        )

        // Adicionar mensagem do usuário
        val userMessageBox = createMessageBox(userMessage)
        messagesContainer.children.add(userMessageBox)

        // Limpar input
        messageInput.clear()

        // Scroll para o final
        Platform.runLater {
            messageArea.vvalue = 1.0
        }

        // Enviar mensagem para o serviço real
        coroutineScope.launch {
            try {
                sendButton.isDisable = true

                val aiResponseText = withContext(Dispatchers.IO) {
                    conversationService.sendMessage(UUID.fromString(currentConversation!!.id), text)
                }

                Platform.runLater {
                    // Calcular tokens da resposta da IA
                    val aiTokens = estimateTokens(aiResponseText)
                    currentTokenCount += aiTokens
                    updateTokenCountLabel()

                    val aiMessage = ChatMessage(
                        content = aiResponseText,
                        isUser = false,
                        timestamp = LocalDateTime.now()
                    )
                    val aiMessageBox = createMessageBox(aiMessage)
                    messagesContainer.children.add(aiMessageBox)

                    Platform.runLater {
                        messageArea.vvalue = 1.0
                    }

                    sendButton.isDisable = messageInput.text.isBlank()
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao enviar mensagem: ${e.message}")
                    sendButton.isDisable = messageInput.text.isBlank()
                }
            }
        }
    }

    private fun createNewConversation() {
        coroutineScope.launch {
            try {
                val newConv = withContext(Dispatchers.IO) {
                    conversationService.createConversation("Nova conversa")
                }

                Platform.runLater {
                    val newConversationItem = DataConverter.toConversationItem(newConv)
                    conversations.add(0, newConversationItem)
                    conversationList.selectionModel.select(0)
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao criar nova conversa: ${e.message}")
                }
            }
        }
    }

    private fun summarizeConversation() {
        val conversation = currentConversation ?: run {
            showError("Nenhuma conversa selecionada")
            return
        }

        // Abrir modal de confirmação antes de processar a sumarização
        showSummaryConfirmation(conversation)
    }

    private fun showSummaryConfirmation(conversation: ConversationItem) {
        try {
            // Criar modal de confirmação programaticamente (sem FXML)
            val confirmationStage = Stage()
            confirmationStage.title = "Confirmar Sumarização"
            confirmationStage.initModality(Modality.APPLICATION_MODAL)
            confirmationStage.initOwner(summarizeButton.scene.window)
            confirmationStage.isResizable = false

            // Criar layout principal
            val rootVBox = VBox()
            rootVBox.styleClass.add("confirmation-modal")
            rootVBox.prefWidth = 500.0
            rootVBox.prefHeight = 300.0

            // Header
            val headerHBox = HBox()
            headerHBox.styleClass.add("confirmation-header")
            headerHBox.prefHeight = 60.0
            headerHBox.spacing = 15.0
            headerHBox.padding = javafx.geometry.Insets(20.0)

            val iconLabel = Label("❓")
            iconLabel.styleClass.add("confirmation-icon")

            val titleLabel = Label("Confirmar Sumarização")
            titleLabel.styleClass.add("confirmation-title")

            val headerSpacer = Region()
            HBox.setHgrow(headerSpacer, javafx.scene.layout.Priority.ALWAYS)

            headerHBox.children.addAll(iconLabel, titleLabel, headerSpacer)

            // Content
            val contentVBox = VBox()
            contentVBox.styleClass.add("confirmation-content")
            contentVBox.spacing = 12.0
            contentVBox.padding = javafx.geometry.Insets(20.0)
            VBox.setVgrow(contentVBox, javafx.scene.layout.Priority.ALWAYS)

            val messageLabel = Label("Deseja gerar um resumo da conversa atual?")
            messageLabel.styleClass.add("confirmation-message")
            messageLabel.isWrapText = true

            val percentage = (currentTokenCount.toDouble() / tokenLimit * 100).toInt()
            val tokenInfoLabel = Label("Conversa atual: $currentTokenCount/$tokenLimit tokens ($percentage%)")
            tokenInfoLabel.styleClass.add("confirmation-info")
            tokenInfoLabel.isWrapText = true

            val warningLabel = Label("O resumo será gerado usando a API DeepSeek IA.")
            warningLabel.styleClass.add("confirmation-warning")
            warningLabel.isWrapText = true

            contentVBox.children.addAll(messageLabel, tokenInfoLabel, warningLabel)

            // Buttons
            val buttonsHBox = HBox()
            buttonsHBox.styleClass.add("confirmation-buttons")
            buttonsHBox.prefHeight = 80.0
            buttonsHBox.spacing = 12.0
            buttonsHBox.padding = javafx.geometry.Insets(15.0, 20.0, 15.0, 20.0)
            buttonsHBox.alignment = javafx.geometry.Pos.CENTER_RIGHT

            val buttonsSpacer = Region()
            HBox.setHgrow(buttonsSpacer, javafx.scene.layout.Priority.ALWAYS)

            val cancelButton = Button("Cancelar")
            cancelButton.styleClass.add("cancel-btn")
            cancelButton.prefWidth = 120.0
            cancelButton.prefHeight = 40.0
            cancelButton.isCancelButton = true

            val confirmButton = Button("Confirmar Sumarização")
            confirmButton.styleClass.add("confirm-btn")
            confirmButton.prefWidth = 180.0
            confirmButton.prefHeight = 40.0
            confirmButton.isDefaultButton = true

            buttonsHBox.children.addAll(buttonsSpacer, cancelButton, confirmButton)

            // Adicionar tudo ao layout principal
            rootVBox.children.addAll(headerHBox, contentVBox, buttonsHBox)

            // Configurar ações dos botões
            cancelButton.setOnAction {
                println("Sumarização cancelada pelo usuário")
                confirmationStage.close()
            }

            confirmButton.setOnAction {
                confirmationStage.close()
                processSummarization(conversation)
            }

            // Criar cena e aplicar estilos
            val scene = Scene(rootVBox, 500.0, 300.0)
            scene.stylesheets.add(javaClass.getResource("/css/main-style.css")?.toExternalForm())
            confirmationStage.scene = scene

            // Centralizar e exibir
            confirmationStage.centerOnScreen()
            confirmationStage.showAndWait()

        } catch (e: Exception) {
            showError("Erro ao abrir confirmação: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun processSummarization(conversation: ConversationItem) {
        summarizeButton.isDisable = true
        alertSummarizeButton.isDisable = true

        coroutineScope.launch {
            try {
                Platform.runLater {
                    // Feedback visual durante processamento
                    summarizeButton.text = "Resumindo..."
                    alertSummarizeButton.text = "Resumindo..."
                }

                // Gerar resumo usando o service
                val conversationSummary = withContext(Dispatchers.IO) {
                    conversationService.createConversationSummary(
                        conversationId = UUID.fromString(conversation.id),
                        tokensUsed = currentTokenCount,
                        summaryMethod = "deepseek"
                    )
                }

                Platform.runLater {
                    currentSummary = conversationSummary.summary
                    openSummaryModal(conversationSummary, conversation)

                    // Ocultar alerta se estiver visível
                    dismissTokenLimitAlert()
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao gerar resumo: ${e.message}")
                }
            } finally {
                Platform.runLater {
                    summarizeButton.isDisable = false
                    alertSummarizeButton.isDisable = false
                    summarizeButton.text = "📝 Resumir"
                    alertSummarizeButton.text = "Resumir Agora"
                }
            }
        }
    }

    private fun openSummaryModal(conversationSummary: ConversationSummarization, conversation: ConversationItem) {
        try {
            val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/summary-modal.fxml"))
            val modalRoot: Parent = fxmlLoader.load()
            val controller = fxmlLoader.getController<SummaryModalController>()

            val modalStage = Stage()
            modalStage.title = "Resumo da Conversa - ${conversation.title}"
            modalStage.initModality(Modality.APPLICATION_MODAL)
            modalStage.initOwner(summarizeButton.scene.window)
            modalStage.isResizable = true
            modalStage.minWidth = 600.0
            modalStage.minHeight = 400.0

            val scene = Scene(modalRoot, 800.0, 600.0)
            scene.stylesheets.add(javaClass.getResource("/css/main-style.css")?.toExternalForm())
            modalStage.scene = scene

            controller.setModalStage(modalStage)
            controller.setSummaryContent(
                summary = conversationSummary.summary,
                conversationInfo = "Conversa: ${conversation.title} | Última atualização: ${conversation.lastMessageTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}",
                tokensUsed = conversationSummary.tokensUsed,
                summaryMethod = conversationSummary.summaryMethod
            )

            controller.setOnNewConversationCallback {
                createNewConversationFromSummary()
            }

            modalStage.showAndWait()
        } catch (e: Exception) {
            showError("Erro ao abrir janela de resumo: ${e.message}")
        }
    }

    private fun createNewConversationFromSummary() {
        val summary = currentSummary ?: return

        try {
            // Simular criação de nova conversa sem persistência real
            Platform.runLater {
                // Criar um item de conversa mockado
                val newConversationId = java.util.UUID.randomUUID().toString()
                val newConversationItem = ConversationItem(
                    id = newConversationId,
                    title = "Nova conversa baseada em resumo",
                    lastMessageTime = LocalDateTime.now()
                )

                // Adicionar à lista de conversas (apenas em memória)
                conversations.add(0, newConversationItem)
                conversationList.selectionModel.select(0)

                // Limpar área de mensagens e mostrar mensagem inicial com resumo
                messagesContainer.children.clear()

                // Criar mensagem inicial com o resumo como contexto
                val initialMessage = ChatMessage(
                    content = "**Contexto da conversa anterior:**\n\n$summary\n\n---\n\n*Nova conversa iniciada com base no resumo acima.*",
                    isUser = false,
                    timestamp = LocalDateTime.now()
                )

                val initialMessageBox = createMessageBox(initialMessage)
                messagesContainer.children.add(initialMessageBox)

                // Resetar contador de tokens
                currentTokenCount = estimateTokens(initialMessage.content)
                updateTokenCountLabel()

                // Limpar resumo atual
                currentSummary = null

                // Feedback visual
                showInfoMessage("Nova conversa criada com base no resumo!")

                println("Nova conversa criada mockada com ID: $newConversationId")
            }
        } catch (e: Exception) {
            Platform.runLater {
                showError("Erro ao criar nova conversa: ${e.message}")
            }
        }
    }

    private fun showInfoMessage(message: String) {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Informação"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }

    private fun dismissTokenLimitAlert() {
        tokenLimitAlert.isVisible = false
        tokenLimitAlert.isManaged = false
    }

    private fun updateTokenCountLabel() {
        Platform.runLater {
            tokenCountLabel.text = "$currentTokenCount/$tokenLimit"

            // Verificar se deve mostrar alerta
            if (currentTokenCount >= tokenWarningThreshold) {
                showTokenWarningAlert()
            }
        }
    }

    private fun showTokenWarningAlert() {
        tokenLimitAlert.isVisible = true
        tokenLimitAlert.isManaged = true

        val percentage = (currentTokenCount.toDouble() / tokenLimit * 100).toInt()
        tokenAlertMessage.text = "Conversa usando $percentage% dos tokens ($currentTokenCount/$tokenLimit)"
    }

    /**
     * A relação NÃO é constante
     *
     * A proporção de ~3,89 caracteres por token é uma média observada
     * em textos em português/inglês, mas varia significativamente dependendo de:
     * Fator	Efeito na relação	Exemplo
     * # 1.
     * - Fator: Idioma
     * - Efeito na relação: Línguas flexionadas (como português) usam mais tokens por palavra
     * - Exemplo: "gatos" = 2 tokens ("gat" + "os")
     * # 2.
     * - Fator: Palavras técnicas
     * - Efeito na relação: Termos técnicos/complexos são divididos em mais tokens
     * - Exemplo: "tokenização" = 4 tokens
     * # 3.
     * - Fator: Números e símbolos
     * - Efeito na relação: Cada símbolo geralmente é um token separado
     * - Exemplo: "42.5%" = 4 tokens
     * # 4.
     * - Fator: Espaços e pontuação
     * - Efeito na relação: Contabilizados como tokens individuais
     * - Exemplo: "\n", "." são tokens
     * # 5.
     * - Fator: Modelo de tokenização
     * - Efeito na relação: Diferentes modelos (BPE, WordPiece) têm regras distintas
     */
    private fun estimateTokens(text: String): Int {
        // Estimativa simples de tokens (aproximadamente 1 token por 4 caracteres para português)
        return (text.length / 4).coerceAtLeast(1)
    }

    private fun showError(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Erro"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}
