package org.hexasilith.service

import org.hexasilith.model.Message
import org.hexasilith.model.Role
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AIService(private val httpClient: HttpClient, private val apiKey: String) {
    suspend fun chatCompletion(messages: List<Message>): Pair<String, String> {
        val request = ChatRequest(
            model = "deepseek-chat",
            messages = messages.map {
                ChatMessage(
                    role = when(it.role) {
                        Role.SYSTEM -> "system"
                        Role.USER -> "user"
                        Role.ASSISTANT -> "assistant"
                    },
                    content = it.content
                )
            },
            temperature = 0.7
        )

        val response = httpClient.post("https://api.deepseek.com/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            header("Accept", "application/json")
            header("Content-Type", "application/json")
            //contentType(ContentType.Application.Json)
            setBody(Json.encodeToString<ChatRequest>(request))
        }

        val rawResponse = rawResponse(response)

        val content = jsonChatResponse(rawResponse)

        // Json.decodeFromString<ChatResponse>(responseText).choices.first().message.content

        return content to rawResponse
    }

    private fun jsonChatResponse(rawResponse: String): String {
        var content = ""
        try {
            content = Json { ignoreUnknownKeys = true }
                .decodeFromString<ChatResponse>(rawResponse)
                .choices
                .first()
                .message
                .content
        } catch (e: Exception) {
            println(e)
        }
        return content
    }

    private suspend fun rawResponse(response: HttpResponse): String {
        var rawResponse = ""
        if(response.status == HttpStatusCode.OK) {
            rawResponse = response.body<String>()
        }
        return rawResponse
    }

    suspend fun summarizeConversation(messages: List<Message>): Pair<String, String> {
        // Preparar o contexto para sumarização
        val conversationText = messages.joinToString("\n") { message ->
            val roleDisplay = when(message.role) {
                Role.USER -> "Usuário"
                Role.ASSISTANT -> "Assistente"
                Role.SYSTEM -> "Sistema"
            }
            "$roleDisplay: ${message.content}"
        }

        val summaryPrompt = """
Você é um assistente especializado em sumarização de conversas. Analise a conversa abaixo e crie um resumo estruturado em formato Markdown.

Conversa a ser sumarizada:
$conversationText

Por favor, crie um resumo seguindo esta estrutura:

## Resumo da Conversa

### 📊 Estatísticas
- Total de mensagens: [número]
- Mensagens do usuário: [número]
- Respostas da IA: [número]

### 🎯 Tópicos Principais
- [Lista dos principais tópicos discutidos]

### 💬 Resumo do Conteúdo
[Parágrafo descritivo sobre o que foi discutido na conversa, destacando os pontos mais importantes e a evolução da discussão]

### ✨ Pontos-Chave
- [Lista dos pontos mais relevantes ou conclusões importantes]

Mantenha o resumo conciso mas informativo, focando nos aspectos mais relevantes da conversa.
        """.trimIndent()

        // Criar mensagem de sistema para sumarização
        val summaryMessages = listOf(
            Message(
                id = java.util.UUID.randomUUID(),
                conversationId = java.util.UUID.randomUUID(),
                role = Role.SYSTEM,
                content = summaryPrompt
            )
        )

        return chatCompletion(summaryMessages)
    }

    @Serializable
    private data class ChatRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val temperature: Double
    )

    @Serializable
    private data class ChatMessage(
        val role: String,
        val content: String
    )

    @Serializable
    private data class ChatResponse(
        val choices: List<Choice>
    ) {
        @Serializable
        data class Choice(
            val message: ChatMessage
        )
    }

}
