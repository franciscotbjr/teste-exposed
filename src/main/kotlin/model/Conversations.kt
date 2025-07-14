package org.hexasilith.model

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Conversations: LongIdTable(name="conversations") {
    val title = varchar("title", length = 256)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updateAt = datetime("update_at").defaultExpression(CurrentDateTime)
}