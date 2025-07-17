package org.hexasilith.presentation

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import kotlinx.serialization.json.Json
import org.hexasilith.config.AppConfig
import org.hexasilith.config.DatabaseConfig
import org.hexasilith.presentation.controller.IntegratedMainController
import org.hexasilith.repository.ApiRawResponseRepository
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.MessageRepository
import org.hexasilith.service.AIService
import org.hexasilith.service.ConversationService

class JavaFXApp : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.title = "DeepSeek AI Chat Client"

        // Configurar dependências
        val httpClient = HttpClient(CIO) {
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

        val conversationRepository = ConversationRepository(DatabaseConfig.database)
        val messageRepository = MessageRepository(DatabaseConfig.database)
        val apiRawResponseRepository = ApiRawResponseRepository(DatabaseConfig.database)
        val aiService = AIService(httpClient, AppConfig.apiKey)
        val conversationService = ConversationService(
            conversationRepository,
            messageRepository,
            apiRawResponseRepository,
            aiService
        )

        val loader = FXMLLoader(javaClass.getResource("/fxml/main-view.fxml"))

        // Configurar controller factory para injetar dependências
        loader.setControllerFactory { _ ->
            IntegratedMainController(conversationService)
        }

        val scene = Scene(loader.load(), 1200.0, 800.0)

        // Aplicar CSS
        scene.stylesheets.add(javaClass.getResource("/css/main-style.css")?.toExternalForm())

        val controller = loader.getController<IntegratedMainController>()
        controller.initialize()

        primaryStage.scene = scene
        primaryStage.minWidth = 800.0
        primaryStage.minHeight = 600.0
        primaryStage.show()
    }
}
