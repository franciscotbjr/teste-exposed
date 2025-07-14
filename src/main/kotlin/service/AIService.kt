package org.hexasilith.service

import org.hexasilith.model.Message
import org.hexasilith.model.Role
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AIService(private val httpClient: HttpClient, private val apiKey: String) {
    suspend fun chatCompletion(messages: List<Message>): String {
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
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString<ChatRequest>(request))
        }

        return response.body<ChatResponse>().choices.first().message.content
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

