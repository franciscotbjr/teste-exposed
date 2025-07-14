package org.hexasilith.model

import java.time.LocalDateTime
import java.util.UUID

data class Message (
    val id: UUID = UUID.randomUUID(),
    val conversationId: UUID,
    val role: Role,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)