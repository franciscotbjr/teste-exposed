package org.hexasilith.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Messages: LongIdTable("messages") {
    val conversationId = reference("conversation_id", Conversations)
    val role = reference("role_id", Roles)
    val content = text("content")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}