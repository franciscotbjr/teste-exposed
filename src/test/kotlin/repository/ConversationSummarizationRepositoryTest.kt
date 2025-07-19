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
import kotlin.test.assertTrue

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

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary)

        // Then
        assertNotNull(conversationSummarization.id)
        assertEquals(testConversationOrigin.id, conversationSummarization.originConversationId)
        assertNull(conversationSummarization.destinyConversationId)
        assertEquals(summary, conversationSummarization.summary)
        assertNotNull(conversationSummarization.createdAt)
    }

    @Test
    fun `should update Conversation Summarization successfully`() {
        // Given
        val summary = "Resumo: O usuário solicitou tradução do termo 'sumarização' para inglês. " +
                "Foi definido como 'Conversation Summarization', " +
                "com alternativas como 'Context Summarization'. " +
                "Discutiu-se também uso em APIs para gerenciamento de tokens."

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary)

        // Then
        assertNotNull(conversationSummarization.id)
        assertEquals(testConversationOrigin.id, conversationSummarization.originConversationId)
        assertNull(conversationSummarization.destinyConversationId)
        assertEquals(summary, conversationSummarization.summary)
        assertNotNull(conversationSummarization.createdAt)

        val testConversationDestiny = conversationRepository.create("Test Destiny Conversation")

        val result = conversationSummarizationRepository.updateDestinyConversationId(
            conversationSummarization.id,
            testConversationDestiny.id)

        assertEquals(result, 1)

    }

    @Test
    fun `should find Conversation Summarization by conversation origin id`() {
        // Given
        val summary = "Resumo: O usuário solicitou tradução do termo 'sumarização' para inglês. " +
                "Foi definido como 'Conversation Summarization', " +
                "com alternativas como 'Context Summarization'. " +
                "Discutiu-se também uso em APIs para gerenciamento de tokens."

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary)

        val conversationSummarizationFound = conversationSummarizationRepository.findByOriginConversationId(testConversationOrigin.id).first()

        // Then
        assertEquals(conversationSummarizationFound?.originConversationId, testConversationOrigin.id)

    }

    @Test
    fun `should find Conversation Summarization by conversation destiny id`() {
        // Given
        val summary = "Resumo: O usuário solicitou tradução do termo 'sumarização' para inglês. " +
                "Foi definido como 'Conversation Summarization', " +
                "com alternativas como 'Context Summarization'. " +
                "Discutiu-se também uso em APIs para gerenciamento de tokens."
        val testConversationDestiny = conversationRepository.create("Test Destiny Conversation")

        // When
        val conversationSummarization = conversationSummarizationRepository.create(
            testConversationOrigin.id,
            summary)

        val result = conversationSummarizationRepository.updateDestinyConversationId(
            conversationSummarization.id,
            testConversationDestiny.id)

        val conversationSummarizationFound = conversationSummarizationRepository.findByDestinyConversationId(testConversationDestiny.id)

        // Then
        assertEquals(conversationSummarizationFound?.originConversationId, testConversationOrigin.id)

    }

    @Test
    fun `should return empty list for non-existent conversation origin`() {
        // Given
        val nonExistentOriginConversationId = UUID.randomUUID()

        // When
        val conversationSummarizations = conversationSummarizationRepository.findByOriginConversationId(nonExistentOriginConversationId)

        // Then
        assertTrue(conversationSummarizations.isEmpty())
    }

    @Test
    fun `should return null for non-existent conversation destiny`() {
        // Given
        val nonExistentDestinyConversationId = UUID.randomUUID()

        // When
        val conversationSummarizations = conversationSummarizationRepository.findByDestinyConversationId(nonExistentDestinyConversationId)

        // Then
        assertNull(conversationSummarizations)
    }

    @AfterAll
    fun cleanup() {
        transaction(database) {
            SchemaUtils.drop(ConversationsSummarizations, Conversations)
        }
        dataSource.close()
    }
}
