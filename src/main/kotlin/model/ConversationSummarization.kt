package org.hexasilith.model

import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.UUID

data class ConversationSummarization (
    val id: UUID = UUID.randomUUID(),
    val originConversationId: UUID, // Referente à conversa que foi sumarizada
    val destinyConversationId: UUID?, // Quando uma nova conversa é criada a partir da sumarização, este campo será preenchido
    val summary: String, // A sumarização da conversa que foi feita
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now() // Data de atualização da sumarização quando uma nova conversa é criada a partir dela
)