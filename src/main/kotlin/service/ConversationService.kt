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
        return conversationRepository.create(generateTitleForConversation(title))
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

        // Chamar a API DeepSeek real para sumarização
        val (summary, _) = aiService.summarizeConversation(messages)

        return summary
    }

    suspend fun createConversationSummary(
        conversationId: UUID,
        tokensUsed: Int = 0,
        summaryMethod: String = "deepseek"
    ): ConversationSummarization {
        val messages = messageRepository.findByConversationId(conversationId)

        if (messages.isEmpty()) {
            throw IllegalArgumentException("Conversa não possui mensagens para sumarizar.")
        }

        // Chamar a API DeepSeek real e capturar a resposta bruta
        val (summary, rawResponse) = aiService.summarizeConversation(messages)

        // Armazenar a resposta bruta da API para auditoria
        apiRawResponseRepository.create(conversationId, rawResponse)

        // Calcular tokens reais baseados no conteúdo da sumarização
        val calculatedTokens = calculateTokensForText(summary)

        // Persistir a sumarização
        return conversationSummarizationRepository.create(
            originConversationId = conversationId,
            summary = summary,
            tokensUsed = calculatedTokens,
            summaryMethod = summaryMethod
        )
    }

    private fun calculateTokensForText(text: String): Int {
        // Estimativa básica de tokens (aproximadamente 1 token por 4 caracteres para inglês/português)
        // Esta é uma estimativa conservadora - em produção seria ideal usar uma biblioteca específica
        return (text.length / 4.0).toInt()
    }

    fun getConversationSummaries(conversationId: UUID, includeInactive: Boolean = false): List<ConversationSummarization> {
        return conversationSummarizationRepository.findByOriginConversationId(conversationId, includeInactive)
    }

    fun deactivateConversationSummary(summaryId: UUID): Boolean {
        return try {
            conversationSummarizationRepository.deactivate(summaryId) > 0
        } catch (e: Exception) {
            false
        }
    }
}