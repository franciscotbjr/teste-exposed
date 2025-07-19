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
import org.hexasilith.presentation.component.MarkdownView

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
    private lateinit var modalStage: Stage

    fun initialize() {
        setupButtons()
    }

    private fun setupButtons() {
        closeSummaryButton.setOnAction { closeModal() }
        newConversationFromSummaryButton.setOnAction { showNewConversationConfirmation() }
        copySummaryButton.setOnAction { copySummaryToClipboard() }
        exportSummaryButton.setOnAction { exportSummary() }
    }

    fun setModalStage(stage: Stage) {
        this.modalStage = stage
    }

    fun setSummaryContent(
        summary: String,
        conversationInfo: String = "",
        tokensUsed: Int = 0,
        summaryMethod: String = "deepseek"
    ) {
        this.summaryText = summary
        this.tokensUsed = tokensUsed
        this.summaryMethod = summaryMethod

        Platform.runLater {
            summaryContent.children.clear()

            val markdownView = MarkdownView()
            markdownView.setMarkdown(summary, isUserMessage = false)
            markdownView.prefWidth = 750.0
            markdownView.maxWidth = 750.0

            summaryContent.children.add(markdownView)

            if (conversationInfo.isNotEmpty()) {
                summaryInfoLabel.text = "$conversationInfo • Tokens: $tokensUsed • Método: $summaryMethod"
            } else {
                summaryInfoLabel.text = "Tokens utilizados: $tokensUsed • Método: $summaryMethod"
            }

            // Scroll para o topo
            summaryScrollPane.vvalue = 0.0
        }
    }

    fun setOnNewConversationCallback(callback: () -> Unit) {
        this.onNewConversationCallback = callback
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
        onNewConversationCallback?.invoke()
        closeModal()
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
            showError("Erro ao copiar para a área de transferência: ${e.message}")
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

    private fun showError(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Erro"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }
}
