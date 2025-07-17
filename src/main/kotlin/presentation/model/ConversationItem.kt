package org.hexasilith.presentation.model

import java.time.LocalDateTime

data class ConversationItem(
    val id: String,
    val title: String,
    val lastMessageTime: LocalDateTime
)
