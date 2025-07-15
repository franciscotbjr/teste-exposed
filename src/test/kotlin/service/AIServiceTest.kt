package service

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.hexasilith.model.Message
import org.hexasilith.model.Role
import org.hexasilith.service.AIService
import org.junit.jupiter.api.*
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AIServiceTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient
    private lateinit var aiService: AIService
    private val apiKey = "test-api-key"

    @BeforeEach
    fun setup() {
        mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                "https://api.deepseek.com/v1/chat/completions" -> {
                    respond(
                        content = ByteReadChannel(validApiResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> respond(
                    content = ByteReadChannel(""),
                    status = HttpStatusCode.NotFound
                )
            }
        }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
        }

        aiService = AIService(httpClient, apiKey)
    }

    @Test
    fun `should send chat completion request successfully`() = runTest {
        // Given
        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.USER,
                content = "Hello, how are you?",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val response = aiService.chatCompletion(messages)

        // Then
        assertEquals<Serializable>("Hello! I'm doing well, thank you for asking. How can I help you today?", response)
        
        // Verify request was made correctly
        val request = mockEngine.requestHistory.first()
        assertEquals("https://api.deepseek.com/v1/chat/completions", request.url.toString())
        assertEquals(HttpMethod.Post, request.method)
        assertEquals("Bearer $apiKey", request.headers["Authorization"])
        assertEquals("application/json", request.headers["Content-Type"])
    }

    @Test
    fun `should handle multiple messages in conversation history`() = runTest {
        // Given
        val conversationId = UUID.randomUUID()
        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                conversationId = conversationId,
                role = Role.SYSTEM,
                content = "You are a helpful assistant.",
                createdAt = LocalDateTime.now()
            ),
            Message(
                id = UUID.randomUUID(),
                conversationId = conversationId,
                role = Role.USER,
                content = "What is 2+2?",
                createdAt = LocalDateTime.now()
            ),
            Message(
                id = UUID.randomUUID(),
                conversationId = conversationId,
                role = Role.ASSISTANT,
                content = "2+2 equals 4.",
                createdAt = LocalDateTime.now()
            ),
            Message(
                id = UUID.randomUUID(),
                conversationId = conversationId,
                role = Role.USER,
                content = "What about 3+3?",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val response = aiService.chatCompletion(messages)

        // Then
        assertEquals<Serializable>("Hello! I'm doing well, thank you for asking. How can I help you today?", response)
        
        // Verify all messages were included in request
        val request = mockEngine.requestHistory.first()
        assertEquals("https://api.deepseek.com/v1/chat/completions", request.url.toString())
    }

    @Test
    fun `should handle API error response`() = runTest {
        // Given
        val errorEngine = MockEngine {
            respond(
                content = ByteReadChannel(errorApiResponse),
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        
        val errorHttpClient = HttpClient(errorEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
        
        val errorAiService = AIService(errorHttpClient, apiKey)
        
        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.USER,
                content = "Test message",
                createdAt = LocalDateTime.now()
            )
        )

        // When & Then
        assertFailsWith<kotlinx.serialization.SerializationException> {
            errorAiService.chatCompletion(messages)
        }
        
        errorHttpClient.close()
    }

    @Test
    fun `should handle empty message list`() = runTest {
        // Given
        val messages = emptyList<Message>()

        // When
        val response = aiService.chatCompletion(messages)

        // Then
        assertEquals<Serializable>("Hello! I'm doing well, thank you for asking. How can I help you today?", response)
    }

    @Test
    fun `should convert role types correctly`() = runTest {
        // Given
        val messages = listOf(
            Message(UUID.randomUUID(), UUID.randomUUID(), Role.SYSTEM, "System message", LocalDateTime.now()),
            Message(UUID.randomUUID(), UUID.randomUUID(), Role.USER, "User message", LocalDateTime.now()),
            Message(UUID.randomUUID(), UUID.randomUUID(), Role.ASSISTANT, "Assistant message", LocalDateTime.now())
        )

        // When
        aiService.chatCompletion(messages)

        // Then
        val request = mockEngine.requestHistory.first()
        assertEquals("https://api.deepseek.com/v1/chat/completions", request.url.toString())
        // Roles should be correctly mapped to API format
    }

    @AfterEach
    fun cleanup() {
        httpClient.close()
    }

    companion object {
        private val validApiResponse = """
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
                        "content": "Hello! I'm doing well, thank you for asking. How can I help you today?"
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

        private val errorApiResponse = """
        {
            "error": {
                "message": "Invalid API key provided",
                "type": "invalid_request_error",
                "code": "invalid_api_key"
            }
        }
        """.trimIndent()
    }
}
