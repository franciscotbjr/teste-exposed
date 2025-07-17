package org.hexasilith.presentation.model

import java.time.LocalDateTime

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: LocalDateTime
)
