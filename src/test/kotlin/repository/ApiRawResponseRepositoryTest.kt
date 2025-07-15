package repository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.hexasilith.model.*
import org.hexasilith.repository.ApiRawResponseRepository
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
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApiRawResponseRepositoryTest {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database
    private lateinit var apiRawResponseRepository: ApiRawResponseRepository
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
            SchemaUtils.create(Roles, Conversations, ApiRawResponses)
            
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
        
        apiRawResponseRepository = ApiRawResponseRepository(database)
        conversationRepository = ConversationRepository(database)
    }

    @BeforeEach
    fun setupTestData() {
        transaction(database) {
            ApiRawResponses.deleteAll()
            Conversations.deleteAll()
        }
        testConversation = conversationRepository.create("Test Conversation")
    }

    @Test
    fun `should create api raw response successfully`() {
        // Given
        val rawJson = """{"choices":[{"message":{"content":"Test response"}}]}"""

        // When
        val response = apiRawResponseRepository.create(testConversation.id, rawJson)

        // Then
        assertNotNull(response.id)
        assertEquals(testConversation.id, response.conversationId)
        assertEquals(rawJson, response.rawJson)
        assertNotNull(response.createdAt)
    }

    @Test
    fun `should find api raw responses by conversation id in correct order`() {
        // Given
        val json1 = """{"response": "first"}"""
        val json2 = """{"response": "second"}"""
        val json3 = """{"response": "third"}"""

        val response1 = apiRawResponseRepository.create(testConversation.id, json1)
        Thread.sleep(10) // Garantir ordem temporal
        val response2 = apiRawResponseRepository.create(testConversation.id, json2)
        Thread.sleep(10)
        val response3 = apiRawResponseRepository.create(testConversation.id, json3)

        // When
        val responses = apiRawResponseRepository.findByConversationId(testConversation.id)

        // Then
        assertEquals(3, responses.size)
        assertEquals(json1, responses[0].rawJson)
        assertEquals(json2, responses[1].rawJson)
        assertEquals(json3, responses[2].rawJson)
    }

    @Test
    fun `should return empty list for non-existent conversation`() {
        // Given
        val nonExistentConversationId = UUID.randomUUID()

        // When
        val responses = apiRawResponseRepository.findByConversationId(nonExistentConversationId)

        // Then
        assertTrue(responses.isEmpty())
    }

    @Test
    fun `should handle large json content`() {
        // Given
        val largeJson = """{"data": "${"A".repeat(50000)}"}"""

        // When
        val response = apiRawResponseRepository.create(testConversation.id, largeJson)

        // Then
        assertEquals(largeJson, response.rawJson)
        
        val retrievedResponses = apiRawResponseRepository.findByConversationId(testConversation.id)
        assertEquals(1, retrievedResponses.size)
        assertEquals(largeJson, retrievedResponses[0].rawJson)
    }

    @Test
    fun `should handle complex json structure`() {
        // Given
        val complexJson = """
        {
            "id": "chatcmpl-123",
            "object": "chat.completion",
            "created": 1677652288,
            "model": "deepseek-chat",
            "choices": [
                {
                    "index": 0,
                    "message": {
                        "role": "assistant",
                        "content": "Hello! How can I help you today?"
                    },
                    "finish_reason": "stop"
                }
            ],
            "usage": {
                "prompt_tokens": 56,
                "completion_tokens": 31,
                "total_tokens": 87
            }
        }
        """.trimIndent()

        // When
        val response = apiRawResponseRepository.create(testConversation.id, complexJson)

        // Then
        assertEquals(complexJson, response.rawJson)
        
        val retrievedResponses = apiRawResponseRepository.findByConversationId(testConversation.id)
        assertEquals(1, retrievedResponses.size)
        assertEquals(complexJson, retrievedResponses[0].rawJson)
    }

    @Test
    fun `should handle multiple responses for same conversation`() {
        // Given
        val json1 = """{"response": "first call"}"""
        val json2 = """{"response": "second call"}"""
        val json3 = """{"response": "third call"}"""

        // When
        apiRawResponseRepository.create(testConversation.id, json1)
        apiRawResponseRepository.create(testConversation.id, json2)
        apiRawResponseRepository.create(testConversation.id, json3)

        // Then
        val responses = apiRawResponseRepository.findByConversationId(testConversation.id)
        assertEquals(3, responses.size)
        
        // Verificar que todas pertencem à mesma conversa
        responses.forEach { response ->
            assertEquals(testConversation.id, response.conversationId)
        }
    }

    @AfterAll
    fun cleanup() {
        transaction(database) {
            SchemaUtils.drop(ApiRawResponses, Conversations, Roles)
        }
        dataSource.close()
    }
}
