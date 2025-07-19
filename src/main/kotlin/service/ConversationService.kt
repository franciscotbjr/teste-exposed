package org.hexasilith.service

import org.hexasilith.model.Conversation
import org.hexasilith.model.ConversationSummarization
import org.hexasilith.model.Message
import org.hexasilith.model.Role
import org.hexasilith.repository.ApiRawResponseRepository
import org.hexasilith.repository.ConversationRepository
import org.hexasilith.repository.ConversationSummarizationRepository
import org.hexasilith.repository.MessageRepository
import java.util.UUID

class ConversationService(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val apiRawResponseRepository: ApiRawResponseRepository,
    private val conversationSummarizationRepository: ConversationSummarizationRepository,
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

        // Simular resumo gerado
        val summary = """
## Resumo da Conversa

**Estatísticas:**
- Total de mensagens: $messageCount
- Mensagens do usuário: $userMessages  
- Respostas da IA: $aiMessages

**Resumo do conteúdo:**
${if (messages.isNotEmpty()) {
    val firstUserMessage = messages.firstOrNull { it.role == Role.USER }?.content ?: ""
    val lastUserMessage = messages.lastOrNull { it.role == Role.USER }?.content ?: ""
    
    "A conversa iniciou com: \"${firstUserMessage.take(100)}...\"\n" +
    if (firstUserMessage != lastUserMessage) {
        "E a última interação foi sobre: \"${lastUserMessage.take(100)}...\"\n"
    } else ""
} else "Não há mensagens para resumir."}

**Tópicos principais discutidos:**
- Interação com IA conversacional
- ${messageCount} trocas de mensagens realizadas
- Conversa ${if (messageCount > 10) "extensa" else "concisa"} com múltiplos pontos abordados
        """.trimIndent()

        return summary
    }

    suspend fun createConversationSummary(
        conversationId: UUID,
        tokensUsed: Int = 0,
        summaryMethod: String = "deepseek"
    ): ConversationSummarization {
        // Gerar o resumo
        val summary = summarizeConversation(conversationId)

        // Persistir a sumarização
        return conversationSummarizationRepository.create(
            originConversationId = conversationId,
            summary = summary,
            tokensUsed = tokensUsed,
            summaryMethod = summaryMethod
        )
    }

    fun getConversationSummaries(conversationId: UUID, includeInactive: Boolean = false): List<ConversationSummarization> {
        return conversationSummarizationRepository.findByOriginConversationId(conversationId, includeInactive)
    }

    fun updateSummaryDestinyConversation(summaryId: UUID, destinyConversationId: UUID): Boolean {
        return try {
            conversationSummarizationRepository.updateDestinyConversationId(summaryId, destinyConversationId) > 0
        } catch (e: Exception) {
            false
        }
    }

    fun deactivateConversationSummary(summaryId: UUID): Boolean {
        return try {
            conversationSummarizationRepository.deactivate(summaryId) > 0
        } catch (e: Exception) {
            false
        }
    }
}