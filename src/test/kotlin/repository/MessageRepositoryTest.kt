package repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hexasilith.model.*
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.MessageRepository
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
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageRepositoryTest {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database
    private lateinit var messageRepository: MessageRepository
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var testConversation: Conversation

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
            SchemaUtils.create(Roles, Conversations, Messages)
            
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
        
        messageRepository = MessageRepository(database)
        conversationRepository = ConversationRepository(database)
    }

    @BeforeEach
    fun setupTestData() {
        transaction(database) {
            Messages.deleteAll()
            Conversations.deleteAll()
        }
        testConversation = conversationRepository.create("Test Conversation")
    }

    @Test
    fun `should create message successfully`() {
        // Given
        val content = "Hello, this is a test message"
        val role = Role.USER

        // When
        val message = messageRepository.create(testConversation.id, role, content)

        // Then
        assertNotNull(message.id)
        assertEquals(testConversation.id, message.conversationId)
        assertEquals(role, message.role)
        assertEquals(content, message.content)
        assertNotNull(message.createdAt)
    }

    @Test
    fun `should find messages by conversation id in correct order`() {
        // Given
        val message1 = messageRepository.create(testConversation.id, Role.USER, "First message")
        Thread.sleep(10) // Garantir ordem temporal
        val message2 = messageRepository.create(testConversation.id, Role.ASSISTANT, "Second message")
        Thread.sleep(10)
        val message3 = messageRepository.create(testConversation.id, Role.USER, "Third message")

        // When
        val messages = messageRepository.findByConversationId(testConversation.id)

        // Then
        assertEquals(3, messages.size)
        assertEquals("First message", messages[0].content)
        assertEquals("Second message", messages[1].content)
        assertEquals("Third message", messages[2].content)
    }

    @Test
    fun `should return empty list for non-existent conversation`() {
        // Given
        val nonExistentConversationId = UUID.randomUUID()

        // When
        val messages = messageRepository.findByConversationId(nonExistentConversationId)

        // Then
        assertTrue(messages.isEmpty())
    }

    @Test
    fun `should handle different role types`() {
        // Given & When
        val systemMessage = messageRepository.create(testConversation.id, Role.SYSTEM, "System message")
        val userMessage = messageRepository.create(testConversation.id, Role.USER, "User message")
        val assistantMessage = messageRepository.create(testConversation.id, Role.ASSISTANT, "Assistant message")

        // Then
        val messages = messageRepository.findByConversationId(testConversation.id)
        assertEquals(3, messages.size)
        
        val systemMsg = messages.find { it.role == Role.SYSTEM }
        val userMsg = messages.find { it.role == Role.USER }
        val assistantMsg = messages.find { it.role == Role.ASSISTANT }
        
        assertNotNull(systemMsg)
        assertNotNull(userMsg)
        assertNotNull(assistantMsg)
        assertEquals("System message", systemMsg.content)
        assertEquals("User message", userMsg.content)
        assertEquals("Assistant message", assistantMsg.content)
    }

    @Test
    fun `should handle long message content`() {
        // Given
        val longContent = "A".repeat(10000) // Mensagem muito longa

        // When
        val message = messageRepository.create(testConversation.id, Role.USER, longContent)

        // Then
        assertEquals(longContent, message.content)
        
        val retrievedMessages = messageRepository.findByConversationId(testConversation.id)
        assertEquals(1, retrievedMessages.size)
        assertEquals(longContent, retrievedMessages[0].content)
    }

    @AfterAll
    fun cleanup() {
        transaction(database) {
            SchemaUtils.drop(Messages, Conversations, Roles)
        }
        dataSource.close()
    }
}
