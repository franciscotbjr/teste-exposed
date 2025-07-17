package org.hexasilith.presentation.service

import kotlinx.coroutines.delay
import org.hexasilith.presentation.model.ChatMessage
import java.time.LocalDateTime
import kotlin.random.Random

class MockChatService {

    private val mockMessages = mutableMapOf<String, MutableList<ChatMessage>>()

    private val aiResponses = listOf(
        "Olá! Como posso ajudá-lo hoje?",
        "Essa é uma pergunta interessante. Deixe-me pensar...",
        "Claro! Posso explicar isso para você.",
        "Entendi sua dúvida. Vou tentar esclarecer.",
        "Ótima pergunta! Aqui está minha resposta detalhada.",
        "Posso ajudar com isso. Vamos começar pelo básico.",
        "Interessante perspectiva. Vou complementar com mais informações.",
        "Concordo com seu ponto de vista. Vou adicionar alguns detalhes.",
        "Essa é uma área complexa, mas vou tentar simplificar.",
        "Baseado no que você disse, sugiro o seguinte..."
    )

    init {
        // Inicializar com algumas mensagens mock
        initializeMockMessages()
    }

    private fun initializeMockMessages() {
        mockMessages["1"] = mutableListOf(
            ChatMessage("Olá! Como você está?", true, LocalDateTime.now().minusHours(2)),
            ChatMessage("Olá! Estou bem, obrigado por perguntar. Como posso ajudá-lo hoje?", false, LocalDateTime.now().minusHours(2).plusMinutes(1)),
            ChatMessage("Preciso de ajuda com programação em Kotlin", true, LocalDateTime.now().minusHours(1)),
            ChatMessage("Claro! Kotlin é uma linguagem moderna e poderosa. Qual aspecto específico você gostaria de aprender?", false, LocalDateTime.now().minusHours(1).plusMinutes(2))
        )

        mockMessages["2"] = mutableListOf(
            ChatMessage("Qual a diferença entre val e var em Kotlin?", true, LocalDateTime.now().minusMinutes(30)),
            ChatMessage("Ótima pergunta! 'val' é para valores imutáveis (só podem ser atribuídos uma vez), enquanto 'var' é para variáveis mutáveis (podem ser reatribuídas). É uma boa prática usar 'val' sempre que possível.", false, LocalDateTime.now().minusMinutes(29))
        )

        mockMessages["3"] = mutableListOf(
            ChatMessage("Como criar uma interface gráfica em JavaFX?", true, LocalDateTime.now().minusMinutes(15)),
            ChatMessage("JavaFX é excelente para criar interfaces gráficas! Você pode usar FXML para definir o layout e Controllers em Kotlin para a lógica. Precisa de algum exemplo específico?", false, LocalDateTime.now().minusMinutes(14))
        )
    }

    suspend fun getMessages(conversationId: String): List<ChatMessage> {
        // Simular delay de carregamento
        delay(200)
        return mockMessages[conversationId] ?: emptyList()
    }

    suspend fun sendMessage(conversationId: String, message: String): ChatMessage {
        // Simular delay de processamento
        delay(Random.nextLong(1000, 3000))

        val aiResponse = ChatMessage(
            content = generateAIResponse(message),
            isUser = false,
            timestamp = LocalDateTime.now()
        )

        // Adicionar mensagem do usuário e resposta da IA à conversa
        val conversation = mockMessages.getOrPut(conversationId) { mutableListOf() }
        conversation.add(ChatMessage(message, true, LocalDateTime.now().minusSeconds(1)))
        conversation.add(aiResponse)

        return aiResponse
    }

    private fun generateAIResponse(userMessage: String): String {
        // Gerar resposta baseada no conteúdo da mensagem
        return when {
            userMessage.contains("kotlin", ignoreCase = true) ->
                "Kotlin é uma linguagem fantástica! ${aiResponses.random()}"
            userMessage.contains("javafx", ignoreCase = true) ->
                "JavaFX é uma biblioteca poderosa para interfaces gráficas. ${aiResponses.random()}"
            userMessage.contains("como", ignoreCase = true) || userMessage.contains("?") ->
                "Vou explicar passo a passo: ${aiResponses.random()}"
            userMessage.contains("obrigado", ignoreCase = true) || userMessage.contains("thanks", ignoreCase = true) ->
                "De nada! Fico feliz em ajudar. Precisa de mais alguma coisa?"
            else -> aiResponses.random()
        }
    }
}
