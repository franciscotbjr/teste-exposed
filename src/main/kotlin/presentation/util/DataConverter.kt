package org.hexasilith.presentation.util

import org.hexasilith.model.Conversation
import org.hexasilith.model.ConversationSummarization
import org.hexasilith.model.Message
import org.hexasilith.presentation.model.ChatMessage
import org.hexasilith.presentation.model.ConversationItem
import org.hexasilith.presentation.model.SummarizationItem

object DataConverter {

    fun toConversationItem(conversation: Conversation): ConversationItem {
        return ConversationItem(
            id = conversation.id.toString(),
            title = conversation.title ?: "Conversa sem título",
            lastMessageTime = conversation.updatedAt
        )
    }

    fun toChatMessage(message: Message): ChatMessage {
        return ChatMessage(
            content = message.content,
            isUser = message.role == org.hexasilith.model.Role.USER,
            timestamp = message.createdAt
        )
    }

    fun toConversationItems(conversations: List<Conversation>): List<ConversationItem> {
        return conversations.map { toConversationItem(it) }
    }

    fun toChatMessages(messages: List<Message>): List<ChatMessage> {
        return messages.map { toChatMessage(it) }
    }

    fun toSummarizationItem(summarization: ConversationSummarization, originConversationTitle: String): SummarizationItem {
        return SummarizationItem(
            id = summarization.id.toString(),
            originConversationId = summarization.originConversationId.toString(),
            originConversationTitle = originConversationTitle,
            summary = summarization.summary,
            tokensUsed = summarization.tokensUsed,
            summaryMethod = summarization.summaryMethod,
            isActive = summarization.isActive,
            createdAt = summarization.createdAt,
            updatedAt = summarization.updatedAt
        )
    }

    fun toSummarizationItems(summarizations: List<ConversationSummarization>, conversationTitles: Map<String, String>): List<SummarizationItem> {
        return summarizations.map { summarization ->
            val originTitle = conversationTitles[summarization.originConversationId.toString()] ?: "Conversa não encontrada"
            toSummarizationItem(summarization, originTitle)
        }
    }
}
