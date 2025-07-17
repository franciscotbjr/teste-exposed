package org.hexasilith.presentation.controller

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hexasilith.presentation.model.ChatMessage
import org.hexasilith.presentation.model.ConversationItem
import org.hexasilith.presentation.util.DataConverter
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

    private val conversations: ObservableList<ConversationItem> = FXCollections.observableArrayList()
    private var currentConversation: ConversationItem? = null
    private val coroutineScope = CoroutineScope(Dispatchers.JavaFx)

    fun initialize() {
        setupConversationList()
        setupMessageInput()
        setupButtons()
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

            // Label da mensagem
            val messageLabel = Label(message.content)
            messageLabel.styleClass.add("user-message")
            messageLabel.isWrapText = true

            // Timestamp
            val timeLabel = Label(message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")))
            timeLabel.styleClass.add("user-message-time")

            messageContent.children.addAll(messageLabel, timeLabel)

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

            // Label da mensagem
            val messageLabel = Label(message.content)
            messageLabel.styleClass.add("ai-message")
            messageLabel.isWrapText = true

            // Timestamp
            val timeLabel = Label(message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")))
            timeLabel.styleClass.add("ai-message-time")

            messageContent.children.addAll(messageLabel, timeLabel)

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

    private fun showError(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Erro"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}
