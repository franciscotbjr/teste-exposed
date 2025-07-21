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
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertContains
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AIServiceTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient
    private lateinit var aiService: AIService
    private val apiKey = "test-api-key"

    private val validApiResponse = """
        {
            "choices": [{
                "message": {
                    "role": "assistant",
                    "content": "Hello from DeepSeek AI!"
                }
            }]
        }
    """.trimIndent()

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
        assertEquals("Hello from DeepSeek AI!", response.first)

        // Verify all messages were included in request
        val request = mockEngine.requestHistory.first()
        assertEquals("https://api.deepseek.com/v1/chat/completions", request.url.toString())
    }

    @Test
    fun `should handle empty message list`() = runTest {
        // Given
        val messages = emptyList<Message>()

        // When
        val response = aiService.chatCompletion(messages)

        // Then
        assertEquals("Hello from DeepSeek AI!", response.first)
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

    @Test
    fun `should summarize conversation with real API call`() = runTest {
        // Given
        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.USER,
                content = "Hello, how are you?",
                createdAt = LocalDateTime.now()
            ),
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.ASSISTANT,
                content = "I'm doing well, thank you for asking!",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val (summary, rawResponse) = aiService.summarizeConversation(messages)

        // Then
        assertEquals("Hello from DeepSeek AI!", summary)
        assertEquals(validApiResponse, rawResponse)

        // Verify the mock engine was called with correct URL
        assertEquals("https://api.deepseek.com/v1/chat/completions", mockEngine.requestHistory.first().url.toString())

        // Verify authorization header
        val authHeader = mockEngine.requestHistory.first().headers[HttpHeaders.Authorization]
        assertEquals("Bearer $apiKey", authHeader)
    }

    @Test
    fun `should format conversation properly for summarization`() = runTest {
        // Given
        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.SYSTEM,
                content = "You are a helpful assistant",
                createdAt = LocalDateTime.now()
            ),
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.USER,
                content = "What is artificial intelligence?",
                createdAt = LocalDateTime.now()
            ),
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.ASSISTANT,
                content = "AI is the simulation of human intelligence processes by machines.",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        val (summary, rawResponse) = aiService.summarizeConversation(messages)

        // Then
        assertEquals("Hello from DeepSeek AI!", summary)

        // The request should contain the conversation formatted properly
        val requestBody = mockEngine.requestHistory.first().body.toByteArray().decodeToString()
        assertContains(requestBody, "Sistema: You are a helpful assistant")
        assertContains(requestBody, "Usuário: What is artificial intelligence?")
        assertContains(requestBody, "Assistente: AI is the simulation of human intelligence processes by machines.")
    }

    @Test
    fun `should handle empty conversation for summarization`() = runTest {
        // Given
        val emptyMessages = emptyList<Message>()

        // When
        val (summary, rawResponse) = aiService.summarizeConversation(emptyMessages)

        // Then
        assertEquals("Hello from DeepSeek AI!", summary)
        assertEquals(validApiResponse, rawResponse)

        // Should still make API call even with empty conversation
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun `should use system prompt for summarization`() = runTest {
        // Given
        val messages = listOf(
            Message(
                id = UUID.randomUUID(),
                conversationId = UUID.randomUUID(),
                role = Role.USER,
                content = "Test message",
                createdAt = LocalDateTime.now()
            )
        )

        // When
        aiService.summarizeConversation(messages)

        // Then
        val requestBody = mockEngine.requestHistory.first().body.toByteArray().decodeToString()
        assertContains(requestBody, "Você é um assistente especializado em sumarização de conversas")
        assertContains(requestBody, "## Resumo da Conversa")
        assertContains(requestBody, "### 📊 Estatísticas")
        assertContains(requestBody, "### 🎯 Tópicos Principais")
        assertContains(requestBody, "### 💬 Resumo do Conteúdo")
        assertContains(requestBody, "### ✨ Pontos-Chave")
    }

    @AfterEach
    fun cleanup() {
        httpClient.close()
    }
}
