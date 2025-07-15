package org.hexasilith.model

import java.time.LocalDateTime
import java.util.UUID

data class ApiRawResponse(
    val id: UUID = UUID.randomUUID(),
    val conversationId: UUID,
    val rawJson: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)