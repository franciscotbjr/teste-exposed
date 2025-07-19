package service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.test.runTest
import org.hexasilith.model.*
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.MessageRepository
import org.hexasilith.repository.ApiRawResponseRepository
import org.hexasilith.repository.ConversationSummarizationRepository
import org.hexasilith.service.AIService
import org.hexasilith.service.ConversationService
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConversationServiceTest {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var messageRepository: MessageRepository
    private lateinit var apiRawResponseRepository: ApiRawResponseRepository
    private lateinit var conversationSummarizationRepository: ConversationSummarizationRepository
    private lateinit var aiService: AIService
    private lateinit var conversationService: ConversationService

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
            SchemaUtils.create(Roles, Conversations, Messages, ApiRawResponses, ConversationsSummarizations)

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
        messageRepository = MessageRepository(database)
        apiRawResponseRepository = ApiRawResponseRepository(database)
        conversationSummarizationRepository = ConversationSummarizationRepository(database)
    }

    @BeforeEach
    fun cleanDatabase() {
        transaction(database) {
            ConversationsSummarizations.deleteAll()
            ApiRawResponses.deleteAll()
            Messages.deleteAll()
            Conversations.deleteAll()
        }
        
        // Create fresh mock for each test
        aiService = mock<AIService>()
        conversationService = ConversationService(
            conversationRepository,
            messageRepository,
            apiRawResponseRepository,
            conversationSummarizationRepository,
            aiService
        )
        
        reset(aiService)
    }

    @Test
    fun `should create conversation successfully`() {
        // Given
        val title = "Test Conversation"

        // When
        val conversation = conversationService.createConversation(title)

        // Then
        assertNotNull(conversation.id)
        assertEquals(title, conversation.title)
        assertNotNull(conversation.createdAt)
        assertNotNull(conversation.updatedAt)
    }

    @Test
    fun `should list all conversations`() {
        // Given
        conversationService.createConversation("Conversation 1")
        conversationService.createConversation("Conversation 2")
        conversationService.createConversation("Conversation 3")

        // When
        val conversations = conversationService.listConversations()

        // Then
        assertEquals(3, conversations.size)
        assertTrue(conversations.any { it.title == "Conversation 1" })
        assertTrue(conversations.any { it.title == "Conversation 2" })
        assertTrue(conversations.any { it.title == "Conversation 3" })
    }

    @Test
    fun `should get conversation by id`() {
        // Given
        val createdConversation = conversationService.createConversation("Test Conversation")

        // When
        val foundConversation = conversationService.getConversation(createdConversation.id)

        // Then
        assertNotNull(foundConversation)
        assertEquals(createdConversation.id, foundConversation.id)
        assertEquals(createdConversation.title, foundConversation.title)
    }

    @Test
    fun `should send message and get AI response`() = runTest {
        // Given
        val conversation = conversationService.createConversation("Test Conversation")
        val userMessage = "Hello, how are you?"
        val aiResponse = "I'm doing well, thank you for asking!"
        val rawResponse = """{"choices":[{"message":{"content":"$aiResponse"}}]}"""
        
        whenever(aiService.chatCompletion(any())).thenReturn(Pair(aiResponse, rawResponse))

        // When
        val response = conversationService.sendMessage(conversation.id, userMessage)

        // Then
        assertEquals(aiResponse, response)
        
        // Verify messages were saved
        val messages = conversationService.getMessages(conversation.id)
        assertEquals(2, messages.size)
        assertEquals(Role.USER, messages[0].role)
        assertEquals(userMessage, messages[0].content)
        assertEquals(Role.ASSISTANT, messages[1].role)
        assertEquals(aiResponse, messages[1].content)
        
        // Verify AI service was called
        verify(aiService).chatCompletion(any())
    }

    @Test
    fun `should update conversation title on first message`() = runTest {
        // Given
        val conversation = conversationService.createConversation("New Conversation")
        val longUserMessage = "This is a very long first message that should be truncated to create a new title for the conversation"
        val aiResponse = "I understand your message!"
        val rawResponse = """{"choices":[{"message":{"content":"$aiResponse"}}]}"""
        
        whenever(aiService.chatCompletion(any())).thenReturn(Pair(aiResponse, rawResponse))

        // When
        conversationService.sendMessage(conversation.id, longUserMessage)

        // Then
        val updatedConversation = conversationService.getConversation(conversation.id)
        assertNotNull(updatedConversation)
        assertEquals(53, updatedConversation.title.length)
        assertTrue(updatedConversation.title.endsWith("..."))
        assertEquals(longUserMessage.take(50) + "...", updatedConversation.title)
    }

    @Test
    fun `should not update title on subsequent messages`() = runTest {
        // Given
        val conversation = conversationService.createConversation("Test Conversation")
        val firstMessage = "First message"
        val secondMessage = "Second message that should not change the title"
        val aiResponse = "AI response"
        val rawResponse = """{"choices":[{"message":{"content":"$aiResponse"}}]}"""
        
        whenever(aiService.chatCompletion(any())).thenReturn(Pair(aiResponse, rawResponse))

        // When
        conversationService.sendMessage(conversation.id, firstMessage)
        val titleAfterFirst = conversationService.getConversation(conversation.id)?.title
        
        conversationService.sendMessage(conversation.id, secondMessage)
        val titleAfterSecond = conversationService.getConversation(conversation.id)?.title

        // Then
        assertEquals(titleAfterFirst, titleAfterSecond)
    }

    @Test
    fun `should handle short first message without truncation`() = runTest {
        // Given
        val conversation = conversationService.createConversation("New Conversation")
        val shortMessage = "Hi!"
        val aiResponse = "Hello!"
        val rawResponse = """{"choices":[{"message":{"content":"$aiResponse"}}]}"""
        
        whenever(aiService.chatCompletion(any())).thenReturn(Pair(aiResponse, rawResponse))

        // When
        conversationService.sendMessage(conversation.id, shortMessage)

        // Then
        val updatedConversation = conversationService.getConversation(conversation.id)
        assertNotNull(updatedConversation)
        assertEquals(shortMessage, updatedConversation.title)
    }

    @Test
    fun `should get messages for conversation`() = runTest {
        // Given
        val conversation = conversationService.createConversation("Test Conversation")
        val message1 = "First message"
        val message2 = "Second message"
        val aiResponse = "AI response"
        val rawResponse = """{"choices":[{"message":{"content":"$aiResponse"}}]}"""
        
        whenever(aiService.chatCompletion(any())).thenReturn(Pair(aiResponse, rawResponse))
        
        conversationService.sendMessage(conversation.id, message1)
        conversationService.sendMessage(conversation.id, message2)

        // When
        val messages = conversationService.getMessages(conversation.id)

        // Then
        assertEquals(4, messages.size) // 2 user messages + 2 AI responses
        assertEquals(Role.USER, messages[0].role)
        assertEquals(message1, messages[0].content)
        assertEquals(Role.ASSISTANT, messages[1].role)
        assertEquals(aiResponse, messages[1].content)
        assertEquals(Role.USER, messages[2].role)
        assertEquals(message2, messages[2].content)
        assertEquals(Role.ASSISTANT, messages[3].role)
        assertEquals(aiResponse, messages[3].content)
    }

    @Test
    fun `should send multiple messages and receive correct AI responses`() = runTest {
        // Given
        val conversation = conversationService.createConversation("Test Conversation")
        val message1 = "Hello, AI!"
        val message2 = "What's the weather like today?"
        val aiResponse1 = "Hello! How can I assist you today?"
        val aiResponse2 = "The weather is sunny with a chance of showers."
        val rawResponse1 = """{"choices":[{"message":{"content":"$aiResponse1"}}]}"""
        val rawResponse2 = """{"choices":[{"message":{"content":"$aiResponse2"}}]}"""
        
        whenever(aiService.chatCompletion(any())).thenReturn(Pair(aiResponse1, rawResponse1), Pair(aiResponse2, rawResponse2))
        
        // First exchange
        conversationService.sendMessage(conversation.id, message1)
        
        // When - Second exchange
        conversationService.sendMessage(conversation.id, message2)

        // Then
        verify(aiService, times(2)).chatCompletion(any())
        
        // Verify second call includes full history
        argumentCaptor<List<Message>>().apply {
            verify(aiService, times(2)).chatCompletion(capture())
            
            val firstCall = allValues[0]
            assertEquals(1, firstCall.size)
            assertEquals(message1, firstCall[0].content)
            
            val secondCall = allValues[1]
            assertEquals(3, secondCall.size)
            assertEquals(message1, secondCall[0].content)
            assertEquals(aiResponse1, secondCall[1].content)
            assertEquals(message2, secondCall[2].content)
        }
    }

    @AfterAll
    fun cleanup() {
        transaction(database) {
            SchemaUtils.drop(ApiRawResponses, Messages, Conversations, Roles)
        }
        dataSource.close()
    }
}
