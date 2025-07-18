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
    private val tokenLimit: Int = 32000 // Limite de tokens para DeepSeek
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
        messages.forEach { message ->
            val messageBox = createMessageBox(message)
            messagesContainer.children.add(messageBox)
        }

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

        summarizeButton.isDisable = true
        alertSummarizeButton.isDisable = true

        coroutineScope.launch {
            try {
                Platform.runLater {
                    // Feedback visual durante processamento
                    summarizeButton.text = "Resumindo..."
                    alertSummarizeButton.text = "Resumindo..."
                }

                // Gerar resumo
                val summary = withContext(Dispatchers.IO) {
                    Thread.sleep(2000) // Simular delay de processamento
                    conversationService.summarizeConversation(UUID.fromString(conversation.id))
                }

                Platform.runLater {
                    currentSummary = summary
                    openSummaryModal(summary, conversation)

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

    private fun openSummaryModal(summary: String, conversation: ConversationItem) {
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
                summary,
                "Conversa: ${conversation.title} | Última atualização: ${conversation.lastMessageTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}"
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

        coroutineScope.launch {
            try {
                val newConv = withContext(Dispatchers.IO) {
                    conversationService.createConversation("Nova conversa baseada em resumo")
                }

                Platform.runLater {
                    val newConversationItem = DataConverter.toConversationItem(newConv)
                    conversations.add(0, newConversationItem)
                    conversationList.selectionModel.select(0)

                    // Resetar contador de tokens
                    currentTokenCount = 0
                    updateTokenCountLabel()

                    currentSummary = null
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao criar nova conversa: ${e.message}")
                }
            }
        }
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
