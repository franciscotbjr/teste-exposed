package org.hexasilith.model

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object Conversations: Table(name="conversations") {
    val id = varchar("id", 36).uniqueIndex().default(UUID.randomUUID().toString())
    val title = varchar("title", length = 256)
    val createdAt = datetime("created_at").default(LocalDateTime.now() )
    val updatedAt = datetime("updated_at").default(LocalDateTime.now() )

    override val primaryKey = PrimaryKey(id)
}