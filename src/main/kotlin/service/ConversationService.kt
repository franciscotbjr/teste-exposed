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
}