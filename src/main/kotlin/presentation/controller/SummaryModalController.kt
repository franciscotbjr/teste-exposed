package org.hexasilith.presentation.controller

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import kotlinx.coroutines.*
import org.hexasilith.model.ConversationSummarization
import org.hexasilith.presentation.component.MarkdownView
import org.hexasilith.service.ConversationService
import java.util.UUID

class SummaryModalController {

    @FXML
    private lateinit var summaryContent: VBox

    @FXML
    private lateinit var summaryScrollPane: ScrollPane

    @FXML
    private lateinit var summaryInfoLabel: Label

    @FXML
    private lateinit var newConversationFromSummaryButton: Button

    @FXML
    private lateinit var closeSummaryButton: Button

    @FXML
    private lateinit var copySummaryButton: Button

    @FXML
    private lateinit var exportSummaryButton: Button

    private var summaryText: String = ""
    private var tokensUsed: Int = 0
    private var summaryMethod: String = "deepseek"
    private var onNewConversationCallback: (() -> Unit)? = null
    private var onConversationNavigationCallback: ((String) -> Unit)? = null
    private lateinit var modalStage: Stage
    private var conversationService: ConversationService? = null
    private var currentConversationId: UUID? = null
    private var currentSummary: ConversationSummarization? = null
    private var summarizationJob: Job? = null

    // Componentes para feedback de progresso
    private var progressIndicator: ProgressIndicator? = null
    private var progressLabel: Label? = null
    private var progressContainer: VBox? = null

    fun initialize() {
        setupButtons()
        setupProgressComponents()
    }

    private fun setupProgressComponents() {
        // Criar componentes de progresso que serão usados durante o carregamento
        progressIndicator = ProgressIndicator().apply {
            prefWidth = 60.0
            prefHeight = 60.0
        }

        progressLabel = Label("Gerando resumo da conversa...").apply {
            styleClass.add("progress-label")
        }

        progressContainer = VBox().apply {
            styleClass.add("progress-container")
            spacing = 20.0
            alignment = javafx.geometry.Pos.CENTER
            children.addAll(progressIndicator, progressLabel)
        }
    }

    fun setConversationService(conversationService: ConversationService) {
        this.conversationService = conversationService
    }

    fun setCurrentConversationId(conversationId: UUID) {
        this.currentConversationId = conversationId
    }

    private fun setupButtons() {
        closeSummaryButton.setOnAction { closeModal() }
        newConversationFromSummaryButton.setOnAction { showNewConversationConfirmation() }
        copySummaryButton.setOnAction { copySummaryToClipboard() }
        exportSummaryButton.setOnAction { exportSummary() }
    }

    fun setModalStage(stage: Stage) {
        this.modalStage = stage

        // Configurar comportamento de fechamento para cancelar operações assíncronas
        stage.setOnCloseRequest {
            summarizationJob?.cancel()
        }
    }

    /**
     * Inicia o processo de sumarização de forma assíncrona
     * Primeiro exibe a tela com indicador de progresso, depois executa as operações
     */
    fun startSummarizationAsync(conversationId: UUID) {
        this.currentConversationId = conversationId

        // 1. Exibir tela imediatamente com indicador de progresso
        showProgressState()

        // 2. Executar sumarização de forma assíncrona
        summarizationJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                updateProgress("Coletando mensagens da conversa...")

                val conversationService = this@SummaryModalController.conversationService
                    ?: throw IllegalStateException("ConversationService não configurado")

                // Simular pequeno delay para garantir que a UI seja mostrada
                delay(100)

                updateProgress("Conectando com a API DeepSeek...")

                // Executar sumarização (operações HTTP e SQL)
                val summarization = conversationService.createConversationSummary(conversationId)

                updateProgress("Processando resumo...")
                delay(200) // Pequeno delay para suavizar a transição

                // Atualizar UI no thread principal
                Platform.runLater {
                    showSummaryResult(summarization)
                }

            } catch (e: CancellationException) {
                Platform.runLater {
                    showError("Operação cancelada pelo usuário")
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao gerar resumo: ${e.message}")
                }
            }
        }
    }

    private suspend fun updateProgress(message: String) {
        Platform.runLater {
            progressLabel?.text = message
        }
    }

    private fun showProgressState() {
        Platform.runLater {
            summaryContent.children.clear()
            progressContainer?.let { summaryContent.children.add(it) }

            // Desabilitar botões durante o carregamento
            newConversationFromSummaryButton.isDisable = true
            copySummaryButton.isDisable = true
            exportSummaryButton.isDisable = true

            // Atualizar info label
            summaryInfoLabel.text = "Gerando resumo automaticamente..."
        }
    }

    private fun showSummaryResult(summarization: ConversationSummarization) {
        this.summaryText = summarization.summary
        this.tokensUsed = summarization.tokensUsed
        this.summaryMethod = summarization.summaryMethod
        this.currentSummary = summarization

        summaryContent.children.clear()

        val markdownView = MarkdownView()
        markdownView.setMarkdown(summarization.summary, isUserMessage = false)
        markdownView.setOnConversationLinkClick { conversationId ->
            onConversationNavigationCallback?.invoke(conversationId)
            closeModal()  // Fechar o modal após navegar
        }
        markdownView.prefWidth = 750.0
        markdownView.maxWidth = 750.0

        summaryContent.children.add(markdownView)

        // Atualizar informações
        val createdAt = summarization.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        summaryInfoLabel.text = "Gerado em $createdAt • Tokens: ${summarization.tokensUsed} • Método: ${summarization.summaryMethod}"

        // Reabilitar botões
        newConversationFromSummaryButton.isDisable = false
        copySummaryButton.isDisable = false
        exportSummaryButton.isDisable = false

        // Scroll para o topo
        summaryScrollPane.vvalue = 0.0
    }

    private fun showError(errorMessage: String) {
        summaryContent.children.clear()

        val errorContainer = VBox().apply {
            styleClass.add("error-container")
            spacing = 15.0
            alignment = javafx.geometry.Pos.CENTER
        }

        val errorIcon = Label("⚠️").apply {
            styleClass.add("error-icon")
            style = "-fx-font-size: 48px;"
        }

        val errorLabel = Label("Erro ao Gerar Resumo").apply {
            styleClass.add("error-title")
        }

        val errorDetails = Label(errorMessage).apply {
            styleClass.add("error-details")
            isWrapText = true
            maxWidth = 600.0
        }

        val retryButton = Button("🔄 Tentar Novamente").apply {
            styleClass.add("retry-btn")
            setOnAction {
                currentConversationId?.let { startSummarizationAsync(it) }
            }
        }

        errorContainer.children.addAll(errorIcon, errorLabel, errorDetails, retryButton)
        summaryContent.children.add(errorContainer)

        // Atualizar info label
        summaryInfoLabel.text = "Falha na geração do resumo"

        // Manter botões desabilitados exceto fechar
        newConversationFromSummaryButton.isDisable = true
        copySummaryButton.isDisable = true
        exportSummaryButton.isDisable = true
    }

    fun setOnNewConversationCallback(callback: () -> Unit) {
        this.onNewConversationCallback = callback
    }

    fun setOnConversationNavigationCallback(callback: (String) -> Unit) {
        this.onConversationNavigationCallback = callback
    }

    private fun closeModal() {
        modalStage.close()
    }

    private fun showNewConversationConfirmation() {
        try {
            // Criar modal de confirmação programaticamente
            val confirmationStage = Stage()
            confirmationStage.title = "Confirmar Nova Conversa"
            confirmationStage.initModality(Modality.APPLICATION_MODAL)
            confirmationStage.initOwner(modalStage)
            confirmationStage.isResizable = false

            // Criar layout principal
            val rootVBox = VBox()
            rootVBox.styleClass.add("confirmation-modal")
            rootVBox.prefWidth = 480.0
            rootVBox.prefHeight = 280.0

            // Header
            val headerHBox = HBox()
            headerHBox.styleClass.add("confirmation-header")
            headerHBox.prefHeight = 60.0
            headerHBox.spacing = 15.0
            headerHBox.padding = javafx.geometry.Insets(20.0)

            val iconLabel = Label("🚀")
            iconLabel.styleClass.add("confirmation-icon")

            val titleLabel = Label("Confirmar Nova Conversa")
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

            val messageLabel = Label("Deseja criar uma nova conversa baseada neste resumo?")
            messageLabel.styleClass.add("confirmation-message")
            messageLabel.isWrapText = true

            val infoLabel = Label("Uma nova conversa será iniciada com o contexto do resumo atual.")
            infoLabel.styleClass.add("confirmation-info")
            infoLabel.isWrapText = true

            val warningLabel = Label("A conversa atual permanecerá salva no histórico.")
            warningLabel.styleClass.add("confirmation-warning")
            warningLabel.isWrapText = true

            contentVBox.children.addAll(messageLabel, infoLabel, warningLabel)

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

            val confirmButton = Button("Confirmar")
            confirmButton.styleClass.add("confirm-btn")
            confirmButton.prefWidth = 120.0
            confirmButton.prefHeight = 40.0
            confirmButton.isDefaultButton = true

            buttonsHBox.children.addAll(buttonsSpacer, cancelButton, confirmButton)

            // Adicionar tudo ao layout principal
            rootVBox.children.addAll(headerHBox, contentVBox, buttonsHBox)

            // Configurar ações dos botões
            cancelButton.setOnAction {
                println("Criação de nova conversa cancelada pelo usuário")
                confirmationStage.close()
            }

            confirmButton.setOnAction {
                confirmationStage.close()
                createNewConversation()
            }

            // Criar cena e aplicar estilos
            val scene = Scene(rootVBox, 480.0, 280.0)
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

    private fun createNewConversation() {
        try {
            val conversationService = this.conversationService
                ?: throw IllegalStateException("ConversationService não configurado")
                
            val currentConversationId = this.currentConversationId
                ?: throw IllegalStateException("ID da conversa atual não está disponível")
                
            val currentSummary = this.currentSummary
                ?: throw IllegalStateException("Resumo atual não está disponível")

            // Criar nova conversa baseada na sumarização
            val newConversation = conversationService.createConversationFromSummary(
                originConversationId = currentConversationId,
                summarizationId = currentSummary.id
            )

            println("Nova conversa criada com sucesso: ${newConversation.id}")
            
            // Notificar callback para atualizar a interface principal
            onNewConversationCallback?.invoke()
            
            closeModal()
            
        } catch (e: Exception) {
            showErrorAlert("Erro ao criar nova conversa: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun copySummaryToClipboard() {
        try {
            val clipboard = Clipboard.getSystemClipboard()
            val content = ClipboardContent()
            content.putString(summaryText)
            clipboard.setContent(content)

            // Feedback visual temporário
            val originalText = copySummaryButton.text
            copySummaryButton.text = "✅ Copiado!"
            copySummaryButton.isDisable = true

            Platform.runLater {
                Thread.sleep(1500)
                Platform.runLater {
                    copySummaryButton.text = originalText
                    copySummaryButton.isDisable = false
                }
            }
        } catch (e: Exception) {
            showErrorAlert("Erro ao copiar para a área de transferência: ${e.message}")
        }
    }

    private fun exportSummary() {
        // Por enquanto, apenas mostrar uma mensagem - será implementado nas próximas fases
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Exportar Resumo"
        alert.headerText = null
        alert.contentText = "Funcionalidade de exportação será implementada em breve."
        alert.showAndWait()
    }

    private fun showErrorAlert(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Erro"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}
