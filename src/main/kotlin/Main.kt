package org.hexasilith

import com.github.ajalt.clikt.core.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.hexasilith.config.AppConfig
import org.hexasilith.config.DatabaseConfig
import org.hexasilith.controller.ChatController
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.MessageRepository
import org.hexasilith.service.AIService
import org.hexasilith.service.ConversationService
import org.hexasilith.util.ConsolePrinter
import org.hexasilith.util.InputReader

fun main(args: Array<String>) = runBlocking {

     // Inicializa o banco de dados local
    DatabaseConfig.database
    val conversationRepository = ConversationRepository(DatabaseConfig.database)
    val messageRepository = MessageRepository(DatabaseConfig.database)

    // Incializa HTTP Client
    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true  // 👈 Esta é a chave para resolver o problema
                isLenient = true
                prettyPrint = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 600_000  // 10 minutos
            socketTimeoutMillis = 600_000   // 10 minutos
            connectTimeoutMillis = 300_000  // 5 minutos
        }
        defaultRequest {
            // Headers específicos para DeepSeek
            header("X-Request-Timeout", "600")  // 10 minutos no header
            header("Keep-Alive", "timeout=600, max=100")
        }
    }

    // Incializa ConversationService
    val aiService = AIService(httpClient, AppConfig.apiKey)
    val conversationService = ConversationService(
        conversationRepository,
        messageRepository,
        aiService
    )

    val consolePrinter = ConsolePrinter()
    val inputReader = InputReader()

    val chatController = ChatController(
        conversationService,
        consolePrinter,
        inputReader
    )

    chatController.start()
}