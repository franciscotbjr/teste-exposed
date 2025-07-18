package org.hexasilith.service

import org.hexasilith.model.Conversation
import org.hexasilith.model.Message
import org.hexasilith.model.Role
import org.hexasilith.repository.ApiRawResponseRepository
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.MessageRepository
import java.util.UUID

class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val apiRawResponseRepository: ApiRawResponseRepository,
    private val aiService: AIService
) {
    fun createConversation(title: String): Conversation {
        return conversationRepository.create(title)
    }

    fun listConversations(): List<Conversation> {
        return conversationRepository.findAll()
    }

    fun getConversation(id: UUID): Conversation? {
        return conversationRepository.findById(id)
    }

    suspend fun sendMessage(conversationId: UUID, userMessage: String): String {
        messageRepository.create(conversationId, Role.USER, userMessage)

        val history = messageRepository.findByConversationId(conversationId)

        val (aiResponse, rawResponse) = aiService.chatCompletion(history)

        apiRawResponseRepository.create(conversationId, rawResponse)

        messageRepository.create(conversationId, Role.ASSISTANT, aiResponse)

        if(history.size == 1) {
            val newTitle = generateTitleForConversation(userMessage)
            conversationRepository.updateTitle(conversationId, newTitle)
        }

        return aiResponse
    }

    suspend fun updateConversationTitle(conversationId: UUID, newTitle: String): Boolean {
        return try {
            conversationRepository.updateTitle(conversationId, generateTitleForConversation(newTitle))
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun generateTitleForConversation(firstMessage: String) : String {
        return firstMessage.take(50).let {
            if (firstMessage.length > 50) "$it..." else it
        }
    }

    fun getMessages(conversationId: UUID): List<Message> {
        return messageRepository.findByConversationId(conversationId)
    }

    suspend fun resendLastUserMessage(conversationId: UUID): String? {
        val messages = messageRepository.findByConversationId(conversationId)

        // Verifica se a última mensagem é do USER
        val lastMessage = messages.lastOrNull()
        if (lastMessage?.role != Role.USER) {
            return null
        }

        val (aiResponse, rawResponse) = aiService.chatCompletion(messages)

        apiRawResponseRepository.create(conversationId, rawResponse)
        messageRepository.create(conversationId, Role.ASSISTANT, aiResponse)

        return aiResponse
    }

    fun hasPendingUserMessage(conversationId: UUID): Boolean {
        val messages = messageRepository.findByConversationId(conversationId)
        return messages.lastOrNull()?.role == Role.USER
    }

    suspend fun summarizeConversation(conversationId: UUID): String {
        val messages = messageRepository.findByConversationId(conversationId)

        if (messages.isEmpty()) {
            return "Nenhuma mensagem encontrada para sumarizar."
        }

        // Por enquanto, vamos criar um resumo mockado
        // Na implementação final, isso será substituído por uma chamada real à API de sumarização
        val messageCount = messages.size
        val userMessages = messages.count { it.role == Role.USER }
        val aiMessages = messages.count { it.role == Role.ASSISTANT }

        val firstUserMessage = messages.firstOrNull { it.role == Role.USER }?.content ?: "Sem mensagem inicial"
        val lastMessage = messages.lastOrNull()?.content ?: "Sem mensagem final"

        return """
## 📝 Resumo da Conversa

**Total de mensagens:** $messageCount
**Mensagens do usuário:** $userMessages
**Mensagens da IA:** $aiMessages

### Primeira mensagem do usuário:
${firstUserMessage.take(100)}${if (firstUserMessage.length > 100) "..." else ""}

### Última mensagem:
${lastMessage.take(100)}${if (lastMessage.length > 100) "..." else ""}

### Resumo:
Esta conversa contém uma interação entre o usuário e a IA DeepSeek. Os tópicos discutidos incluem várias questões e respostas relacionadas aos assuntos apresentados pelo usuário.

### Recomendações:
- Para continuar a discussão, considere criar uma nova conversa
- Os pontos principais podem ser explorados com mais profundidade
- Utilize este resumo como base para futuras interações
        """.trimIndent()
    }
}