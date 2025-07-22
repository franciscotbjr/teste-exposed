package repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hexasilith.model.Conversations
import org.hexasilith.model.Roles
import org.hexasilith.repository.ConversationRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConversationRepositoryTest {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database
    private lateinit var conversationRepository: ConversationRepository

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
            SchemaUtils.create(Roles, Conversations)
            
            // Inserir roles necessários
            Roles.insert {
                it[name] = "SYSTEM"
                it[createdAt] = LocalDateTime.now()
            }
            Roles.insert {
                it[name] = "USER"
                it[createdAt] = LocalDateTime.now()
            }
            Roles.insert {
                it[name] = "ASSISTANT"
                it[createdAt] = LocalDateTime.now()
            }
        }
        
        conversationRepository = ConversationRepository(database)
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction(database) {
            Conversations.deleteAll()
        }
    }

    @Test
    fun `should create conversation successfully`() {
        // Given
        val title = "Test Conversation"

        // When
        val conversation = conversationRepository.create(title)

        // Then
        assertNotNull(conversation.id)
        assertEquals(title, conversation.title)
        assertNotNull(conversation.createdAt)
        assertNotNull(conversation.updatedAt)
    }

    @Test
    fun `should find all conversations`() {
        // Given
        conversationRepository.create("Conversation 1")
        conversationRepository.create("Conversation 2")
        conversationRepository.create("Conversation 3")

        // When
        val conversations = conversationRepository.findAll()

        // Then
        assertEquals(3, conversations.size)
        assertTrue(conversations.any { it.title == "Conversation 1" })
        assertTrue(conversations.any { it.title == "Conversation 2" })
        assertTrue(conversations.any { it.title == "Conversation 3" })
    }

    @Test
    fun `should find conversation by id`() {
        // Given
        val createdConversation = conversationRepository.create("Test Conversation")

        // When
        val foundConversation = conversationRepository.findById(createdConversation.id)

        // Then
        assertNotNull(foundConversation)
        assertEquals(createdConversation.id, foundConversation.id)
        assertEquals(createdConversation.title, foundConversation.title)
    }

    @Test
    fun `should return null when conversation not found by id`() {
        // Given
        val nonExistentId = UUID.randomUUID()

        // When
        val foundConversation = conversationRepository.findById(nonExistentId)

        // Then
        assertNull(foundConversation)
    }

    @Test
    fun `should update conversation title`() {
        // Given
        val conversation = conversationRepository.create("Original Title")
        val newTitle = "Updated Title"

        // When
        conversationRepository.updateTitle(conversation.id, newTitle)
        val updatedConversation = conversationRepository.findById(conversation.id)

        // Then
        assertNotNull(updatedConversation)
        assertEquals(newTitle, updatedConversation.title)
        assertTrue(updatedConversation.updatedAt.isAfter(conversation.updatedAt))
    }

    @Test
    fun `should return empty list when no conversations exist`() {
        // When
        val conversations = conversationRepository.findAll()

        // Then
        assertTrue(conversations.isEmpty())
    }

    @Test
    fun `should create conversation with summarization`() {
        // Given
        val summarizationId = UUID.randomUUID()
        val title = "Test Conversation with Summarization"

        // When
        val conversation = conversationRepository.createWithSummarization(title, summarizationId)

        // Then
        assertNotNull(conversation)
        assertEquals(title, conversation.title)
        assertEquals(summarizationId, conversation.conversationSummarizationId)
        assertNotNull(conversation.id)
        assertNotNull(conversation.createdAt)
        assertNotNull(conversation.updatedAt)
    }

    @Test
    fun `should find conversation with summarization by id`() {
        // Given
        val summarizationId = UUID.randomUUID()
        val title = "Test Conversation with Summarization"
        val createdConversation = conversationRepository.createWithSummarization(title, summarizationId)

        // When
        val foundConversation = conversationRepository.findById(createdConversation.id)

        // Then
        assertNotNull(foundConversation)
        assertEquals(createdConversation.id, foundConversation.id)
        assertEquals(title, foundConversation.title)
        assertEquals(summarizationId, foundConversation.conversationSummarizationId)
    }

    @AfterAll
    fun cleanup() {
        transaction(database) {
            SchemaUtils.drop(Conversations, Roles)
        }
        dataSource.close()
    }
}
