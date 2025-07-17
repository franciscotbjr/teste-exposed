package org.hexasilith.presentation.controller

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import org.hexasilith.presentation.model.ChatMessage
import org.hexasilith.presentation.model.ConversationItem
import org.hexasilith.presentation.service.MockChatService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainController {

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

    private val chatService = MockChatService()
    private val conversations: ObservableList<ConversationItem> = FXCollections.observableArrayList()
    private var currentConversation: ConversationItem? = null
    private val coroutineScope = CoroutineScope(Dispatchers.JavaFx)

    fun initialize() {
        setupConversationList()
        setupMessageInput()
        setupButtons()
        loadMockConversations()
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

        // Desabilitar botão de envio se não houver texto
        messageInput.textProperty().addListener { _, _, newValue ->
            sendButton.isDisable = newValue.isBlank()
        }
        sendButton.isDisable = true
    }

    private fun loadMockConversations() {
        val mockConversations = listOf(
            ConversationItem("1", "Primeira conversa", LocalDateTime.now().minusDays(1)),
            ConversationItem("2", "Dúvidas sobre Kotlin", LocalDateTime.now().minusHours(2)),
            ConversationItem("3", "Projeto JavaFX", LocalDateTime.now().minusMinutes(30))
        )
        conversations.addAll(mockConversations)

        // Selecionar primeira conversa
        if (conversations.isNotEmpty()) {
            conversationList.selectionModel.select(0)
        }
    }

    private fun selectConversation(conversation: ConversationItem) {
        currentConversation = conversation
        loadMessages(conversation)
    }

    private fun loadMessages(conversation: ConversationItem) {
        coroutineScope.launch {
            val messages = chatService.getMessages(conversation.id)
            Platform.runLater {
                displayMessages(messages)
            }
        }
    }

    private fun displayMessages(messages: List<ChatMessage>) {
        messagesContainer.children.clear()
        messages.forEach { message ->
            val messageBox = createMessageBox(message)
            messagesContainer.children.add(messageBox)
        }

        // Scroll para o final
        Platform.runLater {
            messageArea.vvalue = 1.0
        }
    }

    private fun createMessageBox(message: ChatMessage): HBox {
        val messageBox = HBox()
        messageBox.spacing = 10.0

        val messageLabel = Label(message.content)
        messageLabel.isWrapText = true
        messageLabel.maxWidth = 400.0
        messageLabel.styleClass.add(if (message.isUser) "user-message" else "ai-message")

        val timeLabel = Label(message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")))
        timeLabel.styleClass.add("time-label")

        val contentBox = VBox(messageLabel, timeLabel)
        contentBox.spacing = 5.0

        if (message.isUser) {
            messageBox.alignment = Pos.CENTER_RIGHT
            messageBox.children.add(contentBox)
        } else {
            messageBox.alignment = Pos.CENTER_LEFT
            messageBox.children.add(contentBox)
        }

        return messageBox
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

        // Simular resposta da IA
        coroutineScope.launch {
            try {
                sendButton.isDisable = true
                val aiResponse = chatService.sendMessage(currentConversation!!.id, text)

                Platform.runLater {
                    val aiMessageBox = createMessageBox(aiResponse)
                    messagesContainer.children.add(aiMessageBox)

                    // Scroll para o final
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
        val newConversation = ConversationItem(
            id = "conv_${System.currentTimeMillis()}",
            title = "Nova conversa",
            lastMessageTime = LocalDateTime.now()
        )

        conversations.add(0, newConversation)
        conversationList.selectionModel.select(0)
    }

    private fun showError(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Erro"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}
