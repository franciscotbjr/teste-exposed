package org.hexasilith.model

import java.time.LocalDateTime
import java.util.UUID

data class Conversation(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
