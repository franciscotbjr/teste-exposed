package org.hexasilith.presentation.controller

import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage

class SummaryConfirmationController {

    @FXML
    private lateinit var tokenInfoLabel: Label

    @FXML
    private lateinit var cancelButton: Button

    @FXML
    private lateinit var confirmButton: Button

    private var onConfirmCallback: (() -> Unit)? = null
    private var onCancelCallback: (() -> Unit)? = null
    private lateinit var modalStage: Stage
    private var confirmed: Boolean = false

    fun initialize() {
        setupButtons()
    }

    private fun setupButtons() {
        // Configurar ação do botão Cancelar
        cancelButton.setOnAction {
            confirmed = false
            onCancelCallback?.invoke()
            closeModal()
        }

        // Configurar ação do botão Confirmar
        confirmButton.setOnAction {
            confirmed = true
            closeModal() // Fechar primeiro a modal de confirmação
            onConfirmCallback?.invoke() // Depois executar a sumarização
        }

        // Definir botão padrão (Enter confirma)
        confirmButton.isDefaultButton = true

        // Definir botão de cancelamento (ESC cancela)
        cancelButton.isCancelButton = true
    }

    fun setModalStage(stage: Stage) {
        this.modalStage = stage
    }

    fun setTokenInfo(currentTokens: Int, totalTokens: Int) {
        val percentage = (currentTokens.toDouble() / totalTokens * 100).toInt()
        tokenInfoLabel.text = "Conversa atual: $currentTokens/$totalTokens tokens ($percentage%)"
    }

    fun setOnConfirmCallback(callback: () -> Unit) {
        this.onConfirmCallback = callback
    }

    fun setOnCancelCallback(callback: () -> Unit) {
        this.onCancelCallback = callback
    }

    fun isConfirmed(): Boolean {
        return confirmed
    }

    private fun closeModal() {
        modalStage.close()
    }
}
