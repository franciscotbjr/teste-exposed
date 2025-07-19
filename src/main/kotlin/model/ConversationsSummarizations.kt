package org.hexasilith.model

import org.hexasilith.model.Conversations.id
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.util.UUID

object ConversationsSummarizations: Table("conversations_summarizations") {
    val id = varchar("id", 36).default(UUID.randomUUID().toString())
    val originConversationId = varchar("origin_conversation_id", 36).references(Conversations.id, onDelete = ReferenceOption.NO_ACTION)
    val destinyConversationId = varchar("destiny_conversation_id", 36).references(Conversations.id, onDelete = ReferenceOption.NO_ACTION).nullable()
    val summary = text("summary")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}