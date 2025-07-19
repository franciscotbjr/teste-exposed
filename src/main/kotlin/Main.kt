package org.hexasilith

import com.github.ajalt.clikt.core.*
import javafx.application.Application
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
import org.hexasilith.presentation.JavaFXApp
import org.hexasilith.repository.ApiRawResponseRepository
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.ConversationSummarizationRepository
import org.hexasilith.repository.MessageRepository
import org.hexasilith.service.AIService
import org.hexasilith.service.ConversationService
import org.hexasilith.util.ConsolePrinter
import org.hexasilith.util.InputReader

fun main(args: Array<String>) {
    // Inicializa o banco de dados local com Flyway migrations
    println("Inicializando banco de dados e executando migrações...")
    DatabaseConfig.database
    println("Migrações executadas com sucesso!")

    // Verifica se deve usar interface gráfica ou console
    val useGui = args.contains("--gui") || args.contains("-g") || args.isEmpty()

    if (useGui) {
        println("Iniciando interface gráfica JavaFX...")
        Application.launch(JavaFXApp::class.java, *args)
    } else {
        println("Iniciando interface de console...")
        runBlocking {
            startConsoleApp()
        }
    }
}

private suspend fun startConsoleApp() {
    val conversationRepository = ConversationRepository(DatabaseConfig.database)
    val messageRepository = MessageRepository(DatabaseConfig.database)
    val apiRawResponseRepository = ApiRawResponseRepository(DatabaseConfig.database)

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
    val conversationSummarizationRepository = ConversationSummarizationRepository(DatabaseConfig.database)
    val conversationService = ConversationService(
        conversationRepository,
        messageRepository,
        apiRawResponseRepository,
        conversationSummarizationRepository,
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