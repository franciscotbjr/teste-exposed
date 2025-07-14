package org.hexasilith.model

import org.hexasilith.model.Conversations.id
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Messages: Table("messages") {
    val id = varchar("id", 36).uniqueIndex()
    val conversationId = varchar("conversation_id", 36).references(Conversations.id, onDelete = ReferenceOption.NO_ACTION)
    val role = varchar("role", 15).references( Roles.name, onDelete = ReferenceOption.NO_ACTION)
    val content = text("content")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}