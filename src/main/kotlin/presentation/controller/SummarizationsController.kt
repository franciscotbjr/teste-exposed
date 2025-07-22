package org.hexasilith.presentation.controller

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.Stage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.hexasilith.presentation.model.SummarizationItem
import org.hexasilith.presentation.util.DataConverter
import org.hexasilith.presentation.component.MarkdownView
import org.hexasilith.service.ConversationService
import java.time.format.DateTimeFormatter

class SummarizationsController(
    private val conversationService: ConversationService
) {

    @FXML
    private lateinit var summarizationsList: ListView<SummarizationItem>

    @FXML
    private lateinit var contentArea: ScrollPane

    @FXML
    private lateinit var contentContainer: MarkdownView

    @FXML
    private lateinit var backButton: Button

    @FXML
    private lateinit var refreshButton: Button

    @FXML
    private lateinit var selectedSummaryInfoLabel: Label

    private val summarizations: ObservableList<SummarizationItem> = FXCollections.observableArrayList()
    private var currentSummarization: SummarizationItem? = null
    private val coroutineScope = CoroutineScope(Dispatchers.JavaFx)

    // Callback para navegação de volta à tela principal
    var onBackToMainScreen: (() -> Unit)? = null

    // Callback para navegação entre conversas
    var onConversationLinkClick: ((String) -> Unit)? = null

    fun initialize() {
        setupSummarizationsList()
        setupButtons()
        setupContentArea()
        loadSummarizations()
    }

    private fun setupSummarizationsList() {
        summarizationsList.items = summarizations
        summarizationsList.setCellFactory { _ ->
            object : ListCell<SummarizationItem>() {
                override fun updateItem(item: SummarizationItem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {
                        text = item.getDisplayTitle()
                        tooltip = Tooltip(buildString {
                            appendLine("Conversa: ${item.originConversationTitle}")
                            appendLine("Data: ${item.getFormattedDate()} às ${item.getFormattedTime()}")
                            appendLine("Tokens: ${item.getFormattedTokens()}")
                            appendLine("Método: ${item.summaryMethod}")
                            appendLine("Status: ${item.getStatusText()}")
                            appendLine()
                            append("Preview: ${item.getDisplaySummary()}")
                        })
                    }
                }
            }
        }

        summarizationsList.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            newValue?.let { selectSummarization(it) }
        }
    }

    private fun setupButtons() {
        backButton.setOnAction {
            onBackToMainScreen?.invoke()
        }

        refreshButton.setOnAction {
            loadSummarizations()
        }
    }

    private fun setupContentArea() {
        // Configurar o MarkdownView para exibir conteúdo de sumarização
        contentContainer.setOnConversationLinkClick { conversationId ->
            onConversationLinkClick?.invoke(conversationId)
        }
        
        // Inicialmente, exibir mensagem de instrução
        showWelcomeMessage()
    }

    private fun showWelcomeMessage() {
        val welcomeText = """
        # Visualizador de Sumarizações
        
        **Bem-vindo à tela de sumarizações!**
        
        Aqui você pode:
        - Visualizar todas as sumarizações criadas
        - Ler o conteúdo completo de cada resumo
        - Navegar para conversas relacionadas através dos links
        - Atualizar a lista com o botão "Atualizar"
        
        **Como usar:**
        1. Selecione uma sumarização na lista à esquerda
        2. O conteúdo será exibido nesta área
        3. Clique em links para navegar entre conversas
        4. Use o botão "Voltar" para retornar à tela principal
        
        ---
        
        *Selecione uma sumarização para começar*
        """.trimIndent()
        
        contentContainer.setMarkdown(welcomeText)
        selectedSummaryInfoLabel.text = "Nenhuma sumarização selecionada"
    }

    private fun loadSummarizations() {
        coroutineScope.launch {
            try {
                val (summariesData, conversationTitles) = withContext(Dispatchers.IO) {
                    val summaries = conversationService.getAllSummarizations(includeInactive = false)
                    val titles = conversationService.getConversationTitles()
                    Pair(summaries, titles)
                }

                Platform.runLater {
                    summarizations.clear()
                    
                    if (summariesData.isEmpty()) {
                        summarizations.clear()
                        showEmptyState()
                    } else {
                        summarizations.addAll(
                            DataConverter.toSummarizationItems(summariesData, conversationTitles)
                        )
                        
                        // Se não há seleção, mostrar mensagem de boas-vindas
                        if (currentSummarization == null) {
                            showWelcomeMessage()
                        }
                    }
                }
            } catch (e: Exception) {
                Platform.runLater {
                    showError("Erro ao carregar sumarizações: ${e.message}")
                }
            }
        }
    }

    private fun showEmptyState() {
        val emptyStateText = """
        # Nenhuma Sumarização Encontrada
        
        **Ainda não há sumarizações criadas.**
        
        Para criar uma sumarização:
        1. Volte para a tela principal
        2. Abra uma conversa
        3. Clique no botão "📝 Resumir"
        4. Confirme a criação da sumarização
        
        As sumarizações criadas aparecerão aqui para visualização.
        
        ---
        
        *Use o botão "Voltar" para retornar à tela principal*
        """.trimIndent()
        
        contentContainer.setMarkdown(emptyStateText)
        selectedSummaryInfoLabel.text = "Nenhuma sumarização disponível"
    }

    private fun selectSummarization(summarization: SummarizationItem) {
        currentSummarization = summarization
        displaySummaryContent(summarization)
        updateSummaryInfo(summarization)
    }

    private fun displaySummaryContent(summarization: SummarizationItem) {
        // Criar conteúdo enriquecido com metadados da sumarização
        val enrichedContent = buildString {
            appendLine("# Sumarização da Conversa")
            appendLine()
            appendLine("**Conversa Original:** ${summarization.originConversationTitle}")
            appendLine("**Data de Criação:** ${summarization.getFormattedDate()} às ${summarization.getFormattedTime()}")
            appendLine("**Tokens Utilizados:** ${summarization.getFormattedTokens()}")
            appendLine("**Método:** ${summarization.summaryMethod}")
            appendLine("**Status:** ${summarization.getStatusText()}")
            appendLine()
            appendLine("---")
            appendLine()
            append(summarization.summary)
        }
        
        contentContainer.setMarkdown(enrichedContent)
    }

    private fun updateSummaryInfo(summarization: SummarizationItem) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        selectedSummaryInfoLabel.text = "Selecionada: ${summarization.originConversationTitle} - ${summarization.createdAt.format(formatter)}"
    }

    private fun showError(message: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Erro"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }

    /**
     * Método para selecionar uma sumarização específica por ID
     * Útil para navegação direta de outras telas
     */
    fun selectSummarizationById(summarizationId: String) {
        val targetSummarization = summarizations.find { it.id == summarizationId }
        if (targetSummarization != null) {
            summarizationsList.selectionModel.select(targetSummarization)
            selectSummarization(targetSummarization)
        } else {
            // Se não encontrar, recarregar lista e tentar novamente
            loadSummarizations()
            Platform.runLater {
                val reloadedSummarization = summarizations.find { it.id == summarizationId }
                if (reloadedSummarization != null) {
                    summarizationsList.selectionModel.select(reloadedSummarization)
                    selectSummarization(reloadedSummarization)
                } else {
                    showError("Sumarização não encontrada: $summarizationId")
                }
            }
        }
    }
}