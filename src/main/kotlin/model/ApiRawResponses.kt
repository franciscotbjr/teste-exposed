package org.hexasilith.model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object ApiRawResponses : Table() {
    val id = varchar("id", 36).default(UUID.randomUUID().toString())
    val conversationId = varchar("conversation_id", 36).references(Conversations.id, onDelete = ReferenceOption.NO_ACTION)
    val rawJson = text("raw_json")
    val createdAt = datetime("created_at").default(LocalDateTime.now() )

    override val primaryKey = PrimaryKey(Conversations.id)
}