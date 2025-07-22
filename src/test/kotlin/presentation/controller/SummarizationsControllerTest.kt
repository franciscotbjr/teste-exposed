package presentation.controller

import org.hexasilith.model.ConversationSummarization
import org.hexasilith.presentation.controller.SummarizationsController
import org.hexasilith.presentation.model.SummarizationItem
import org.hexasilith.presentation.util.DataConverter
import org.hexasilith.service.ConversationService
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Testes unitários para SummarizationsController
 * Foca na lógica de negócio e transformação de dados, não nos componentes JavaFX
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SummarizationsControllerTest {

    private lateinit var conversationService: ConversationService
    private lateinit var summarizationsController: SummarizationsController

    @BeforeEach
    fun setup() {
        conversationService = mock()
        // Note: Não podemos criar instâncias do controller que dependem de JavaFX 
        // sem inicializar a plataforma. Estes testes focam na lógica de dados.
    }

    @Test
    @DisplayName("DataConverter deve converter ConversationSummarization para SummarizationItem corretamente")
    fun `should convert ConversationSummarization to SummarizationItem correctly`() {
        // Given
        val summarizationId = UUID.randomUUID()
        val originConversationId = UUID.randomUUID()
        val createdAt = LocalDateTime.of(2025, 1, 15, 10, 30)
        val updatedAt = LocalDateTime.of(2025, 1, 15, 11, 30)

        val conversationSummarization = ConversationSummarization(
            id = summarizationId,
            originConversationId = originConversationId,
            summary = "Este é um resumo de teste da conversa",
            tokensUsed = 150,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        val originTitle = "Conversa sobre IA"

        // When
        val result = DataConverter.toSummarizationItem(conversationSummarization, originTitle)

        // Then
        assertEquals(summarizationId.toString(), result.id)
        assertEquals(originConversationId.toString(), result.originConversationId)
        assertEquals(originTitle, result.originConversationTitle)
        assertEquals("Este é um resumo de teste da conversa", result.summary)
        assertEquals(150, result.tokensUsed)
        assertEquals("deepseek", result.summaryMethod)
        assertTrue(result.isActive)
        assertEquals(createdAt, result.createdAt)
        assertEquals(updatedAt, result.updatedAt)
    }

    @Test
    @DisplayName("SummarizationItem deve gerar título de exibição corretamente")
    fun `should generate display title correctly`() {
        // Given
        val item = SummarizationItem(
            id = UUID.randomUUID().toString(),
            originConversationId = UUID.randomUUID().toString(),
            originConversationTitle = "Conversa sobre Machine Learning",
            summary = "Resumo da conversa",
            tokensUsed = 200,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.of(2025, 1, 15, 14, 30),
            updatedAt = LocalDateTime.of(2025, 1, 15, 14, 30)
        )

        // When
        val displayTitle = item.getDisplayTitle()

        // Then
        assertTrue(displayTitle.contains("Resumo de \"Conversa sobre Machine Learning\""))
        assertTrue(displayTitle.contains("15/01/2025 14:30"))
    }

    @Test
    @DisplayName("SummarizationItem deve gerar resumo de exibição corretamente")
    fun `should generate display summary correctly`() {
        // Given
        val longSummary = "Esta é uma linha muito longa que deveria ser truncada para o preview porque excede o limite de 100 caracteres que é definido para a visualização inicial"
        val item = SummarizationItem(
            id = UUID.randomUUID().toString(),
            originConversationId = UUID.randomUUID().toString(),
            originConversationTitle = "Conversa",
            summary = longSummary,
            tokensUsed = 100,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // When
        val displaySummary = item.getDisplaySummary()

        // Then
        assertEquals(103, displaySummary.length) // 100 characters + "..."
        assertTrue(displaySummary.endsWith("..."))
        assertTrue(displaySummary.startsWith("Esta é uma linha muito longa"))
    }

    @Test
    @DisplayName("SummarizationItem deve formatar tokens corretamente")
    fun `should format tokens correctly`() {
        // Given
        val item = SummarizationItem(
            id = UUID.randomUUID().toString(),
            originConversationId = UUID.randomUUID().toString(),
            originConversationTitle = "Conversa",
            summary = "Resumo",
            tokensUsed = 1250,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        // When
        val formattedTokens = item.getFormattedTokens()

        // Then
        assertEquals("1250 tokens", formattedTokens)
    }

    @Test
    @DisplayName("SummarizationItem deve formatar datas corretamente")
    fun `should format dates correctly`() {
        // Given
        val testDate = LocalDateTime.of(2025, 1, 15, 9, 45)
        val item = SummarizationItem(
            id = UUID.randomUUID().toString(),
            originConversationId = UUID.randomUUID().toString(),
            originConversationTitle = "Conversa",
            summary = "Resumo",
            tokensUsed = 100,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = testDate,
            updatedAt = testDate
        )

        // When
        val formattedDate = item.getFormattedDate()
        val formattedTime = item.getFormattedTime()

        // Then
        assertEquals("15/01/2025", formattedDate)
        assertEquals("09:45", formattedTime)
    }

    @Test
    @DisplayName("SummarizationItem deve mostrar status corretamente")
    fun `should show status correctly`() {
        // Given
        val activeItem = SummarizationItem(
            id = UUID.randomUUID().toString(),
            originConversationId = UUID.randomUUID().toString(),
            originConversationTitle = "Conversa",
            summary = "Resumo",
            tokensUsed = 100,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val inactiveItem = activeItem.copy(isActive = false)

        // When & Then
        assertEquals("Ativo", activeItem.getStatusText())
        assertEquals("Inativo", inactiveItem.getStatusText())
    }

    @Test
    @DisplayName("DataConverter deve converter lista de sumarizações com títulos corretamente")
    fun `should convert summarization list with titles correctly`() {
        // Given
        val summarization1 = ConversationSummarization(
            id = UUID.randomUUID(),
            originConversationId = UUID.randomUUID(),
            summary = "Primeiro resumo",
            tokensUsed = 100,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val summarization2 = ConversationSummarization(
            id = UUID.randomUUID(),
            originConversationId = UUID.randomUUID(),
            summary = "Segundo resumo",
            tokensUsed = 200,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val summarizations = listOf(summarization1, summarization2)
        val conversationTitles = mapOf(
            summarization1.originConversationId.toString() to "Primeira Conversa",
            summarization2.originConversationId.toString() to "Segunda Conversa"
        )

        // When
        val result = DataConverter.toSummarizationItems(summarizations, conversationTitles)

        // Then
        assertEquals(2, result.size)
        assertEquals("Primeira Conversa", result[0].originConversationTitle)
        assertEquals("Segunda Conversa", result[1].originConversationTitle)
        assertEquals("Primeiro resumo", result[0].summary)
        assertEquals("Segundo resumo", result[1].summary)
    }

    @Test
    @DisplayName("DataConverter deve lidar com títulos de conversa não encontrados")
    fun `should handle missing conversation titles`() {
        // Given
        val summarization = ConversationSummarization(
            id = UUID.randomUUID(),
            originConversationId = UUID.randomUUID(),
            summary = "Resumo teste",
            tokensUsed = 150,
            summaryMethod = "deepseek",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val summarizations = listOf(summarization)
        val conversationTitles = emptyMap<String, String>() // Não contém o título

        // When
        val result = DataConverter.toSummarizationItems(summarizations, conversationTitles)

        // Then
        assertEquals(1, result.size)
        assertEquals("Conversa não encontrada", result[0].originConversationTitle)
    }
}