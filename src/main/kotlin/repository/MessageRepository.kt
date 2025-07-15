package org.hexasilith.repository

import org.hexasilith.model.Message
import org.hexasilith.model.Messages
import org.hexasilith.model.Role
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.UUID

class MessageRepository(private val database: Database) {

    fun create(conversationId: UUID, role: Role, content: String): Message = transaction(database) {
        Messages.insert {
            it[this.id] = UUID.randomUUID().toString()
            it[this.conversationId] = conversationId.toString()
            it[this.role] = role.toString()
            it[this.content] = content
        }.let {
            Message(
                id = UUID.fromString(it[Messages.id]),
                conversationId = UUID.fromString(it[Messages.conversationId]),
                role = Role.fromString(it[Messages.role]),
                content = it[Messages.content],
                createdAt = LocalDateTime.now()
            )
        }
    }

    fun findByConversationId(conversationId: UUID): List<Message> = transaction(database) {
        Messages.selectAll()
            .where { Messages.conversationId eq conversationId.toString() }
            .orderBy(Messages.createdAt to SortOrder.ASC)
            .map {
                Message(
                    UUID.fromString(it[Messages.id]),
                    UUID.fromString(it[Messages.conversationId]),
                    Role.fromString(it[Messages.role]),
                    it[Messages.content],
                    it[Messages.createdAt]
                )
            }
    }
}