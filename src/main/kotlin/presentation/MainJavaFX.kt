package org.hexasilith.presentation

import javafx.application.Application
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.hexasilith.config.AppConfig
import org.hexasilith.config.DatabaseConfig
import org.hexasilith.repository.ApiRawResponseRepository
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.MessageRepository
import org.hexasilith.service.AIService
import org.hexasilith.service.ConversationService

object Dependencies {
    val httpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 600_000
                socketTimeoutMillis = 600_000
                connectTimeoutMillis = 300_000
            }
            defaultRequest {
                header("X-Request-Timeout", "600")
                header("Keep-Alive", "timeout=600, max=100")
            }
        }
    }

    val conversationRepository by lazy { ConversationRepository(DatabaseConfig.database) }
    val messageRepository by lazy { MessageRepository(DatabaseConfig.database) }
    val apiRawResponseRepository by lazy { ApiRawResponseRepository(DatabaseConfig.database) }
    val aiService by lazy { AIService(httpClient, AppConfig.apiKey) }
    val conversationService by lazy {
        ConversationService(
            conversationRepository,
            messageRepository,
            apiRawResponseRepository,
            aiService
        )
    }
}

fun main(args: Array<String>) {
    // Inicializar banco de dados
    println("Inicializando banco de dados e executando migrações...")
    DatabaseConfig.database
    println("Migrações executadas com sucesso!")

    // Iniciar aplicação JavaFX
    Application.launch(JavaFXApp::class.java, *args)
}
