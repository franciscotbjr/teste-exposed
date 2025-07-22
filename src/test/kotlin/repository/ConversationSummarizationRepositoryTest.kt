package repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hexasilith.model.*
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.ConversationSummarizationRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConversationSummarizationRepositoryTest {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database
    private lateinit var conversationSummarizationRepository: ConversationSummarizationRepository
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var testConversationOrigin: Conversation

    @BeforeAll
    fun setupDatabase() {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = "jdbc:sqlite::memory:"
            driverClassName = "org.sqlite.JDBC"
            maximumPoolSize = 1
            isAutoCommit = false
            validate()
        }
        
        dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource)
        
        transaction(database) {
            SchemaUtils.create(ConversationsSummarizations, Conversations)
        }

        conversationSummarizationRepository = ConversationSummarizationRepository(database)
        conversationRepository = ConversationRepository(database)
    }

    @BeforeEach
    fun setupTestData() {
        transaction(database) {
            ConversationsSummarizations.deleteAll()
            Conversations.deleteAll()
        }
        testConversationOrigin = conversationRepository.create("Test Origin Conversation")

    }

    @Test
    fun `should create Conversation Summarization successfully`() {
        // Given
        val summary = "Resumo: O usuário solicitou tradução do termo 'sumarização' para inglês. " +
                "Foi definido como 'Conversation Summarization', " +
                "com alternativas como 'Context Summarization'. " +
                "Discutiu-se também uso em APIs para gerenciamento de tokens."
        val tokensUsed = 150
        val summaryMethod = "deepseek"

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary,
            tokensUsed,
            summaryMethod)

        // Then
        assertNotNull(conversationSummarization.id)
        assertEquals(testConversationOrigin.id, conversationSummarization.originConversationId)
        assertEquals(summary, conversationSummarization.summary)
        assertEquals(tokensUsed, conversationSummarization.tokensUsed)
        assertEquals(summaryMethod, conversationSummarization.summaryMethod)
        assertTrue(conversationSummarization.isActive)
        assertNotNull(conversationSummarization.createdAt)
    }

    @Test
    fun `should create Conversation Summarization with default values successfully`() {
        // Given
        val summary = "Resumo básico da conversa"

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary)

        // Then
        assertNotNull(conversationSummarization.id)
        assertEquals(testConversationOrigin.id, conversationSummarization.originConversationId)
        assertEquals(summary, conversationSummarization.summary)
        assertEquals(0, conversationSummarization.tokensUsed)
        assertEquals("deepseek", conversationSummarization.summaryMethod)
        assertTrue(conversationSummarization.isActive)
        assertNotNull(conversationSummarization.createdAt)
    }

    @Test
    fun `should deactivate Conversation Summarization successfully`() {
        // Given
        val summary = "Resumo a ser desativado"
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary)

        // When
        val result = conversationSummarizationRepository.deactivate(conversationSummarization.id)

        // Then
        assertEquals(1, result)

        // Verificar se não aparece na busca normal (apenas ativos)
        val activeSummarizations = conversationSummarizationRepository.findByOriginConversationId(testConversationOrigin.id)
        assertTrue(activeSummarizations.isEmpty())

        // Verificar se aparece na busca incluindo inativos
        val allSummarizations = conversationSummarizationRepository.findByOriginConversationId(testConversationOrigin.id, includeInactive = true)
        assertEquals(1, allSummarizations.size)
        assertFalse(allSummarizations.first().isActive)
    }

    @Test
    fun `should find Conversation Summarization by conversation origin id`() {
        // Given
        val summary = "Resumo: O usuário solicitou tradução do termo 'sumarização' para inglês. " +
                "Foi definido como 'Conversation Summarization', " +
                "com alternativas como 'Context Summarization'. " +
                "Discutiu-se também uso em APIs para gerenciamento de tokens."
        val tokensUsed = 175

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary,
            tokensUsed)

        val conversationSummarizationFound = conversationSummarizationRepository.findByOriginConversationId(testConversationOrigin.id).first()

        // Then
        assertEquals(conversationSummarizationFound.originConversationId, testConversationOrigin.id)
        assertEquals(tokensUsed, conversationSummarizationFound.tokensUsed)
        assertTrue(conversationSummarizationFound.isActive)

    }

    @Test
    fun `should find conversation summarization by id`() {
        // Given
        val testConversationOrigin = conversationRepository.create("Origin Conversation")
        val summary = "This is a test summary"
        val tokensUsed = 150
        
        // When
        val createdSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary,
            tokensUsed
        )
        
        val foundSummarization = conversationSummarizationRepository.findById(createdSummarization.id)
        
        // Then
        assertNotNull(foundSummarization)
        assertEquals(createdSummarization.id, foundSummarization.id)
        assertEquals(summary, foundSummarization.summary)
        assertEquals(tokensUsed, foundSummarization.tokensUsed)
        assertEquals(testConversationOrigin.id, foundSummarization.originConversationId)
        assertTrue(foundSummarization.isActive)
    }

    @Test
    fun `should return null when summarization not found by id`() {
        // Given
        val nonExistentId = UUID.randomUUID()
        
        // When
        val foundSummarization = conversationSummarizationRepository.findById(nonExistentId)
        
        // Then
        assertNull(foundSummarization)
    }

    @AfterAll
    fun cleanup() {
        dataSource.close()
    }

}
