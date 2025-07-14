package org.hexasilith.model

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Messages: UUIDTable("messages") {
    val conversationId = uuid("conversation_id").references(Conversations.id, onDelete = ReferenceOption.NO_ACTION)
    val role = varchar("role", 15).references( Roles.name, onDelete = ReferenceOption.NO_ACTION)
    val content = text("content")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}