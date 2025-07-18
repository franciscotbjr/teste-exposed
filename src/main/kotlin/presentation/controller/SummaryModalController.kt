package org.hexasilith.presentation.controller

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.VBox
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
    private var onNewConversationCallback: (() -> Unit)? = null
    private lateinit var modalStage: Stage

    fun initialize() {
        setupButtons()
    }

    private fun setupButtons() {
        closeSummaryButton.setOnAction { closeModal() }
        newConversationFromSummaryButton.setOnAction { createNewConversation() }
        copySummaryButton.setOnAction { copySummaryToClipboard() }
        exportSummaryButton.setOnAction { exportSummary() }
    }

    fun setModalStage(stage: Stage) {
        this.modalStage = stage
    }

    fun setSummaryContent(summary: String, conversationInfo: String = "") {
        this.summaryText = summary

        Platform.runLater {
            summaryContent.children.clear()

            val markdownView = MarkdownView()
            markdownView.setMarkdown(summary, isUserMessage = false)
            markdownView.prefWidth = 750.0
            markdownView.maxWidth = 750.0

            summaryContent.children.add(markdownView)

            if (conversationInfo.isNotEmpty()) {
                summaryInfoLabel.text = conversationInfo
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
