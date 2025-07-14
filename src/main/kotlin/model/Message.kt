package org.hexasilith.model

import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.UUID

data class Message (
    val id: EntityID<UUID>,
    val conversationId: UUID,
    val role: Role,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)