package org.hexasilith.model

import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.UUID

data class ConversationSummarization (
    val id: UUID = UUID.randomUUID(),
    val originConversationId: UUID,
    val destinyConversationId: UUID?,
    val summary: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)