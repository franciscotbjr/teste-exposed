package org.hexasilith.presentation.util

import org.hexasilith.model.Conversation
import org.hexasilith.model.Message
import org.hexasilith.presentation.model.ChatMessage
import org.hexasilith.presentation.model.ConversationItem

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
}
