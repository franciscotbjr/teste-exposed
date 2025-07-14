package org.hexasilith.repository

import org.hexasilith.model.Message
import org.hexasilith.model.Messages
import org.hexasilith.model.Role
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class MessageRepository(private val database: Database) {

    init {
        transaction {
            SchemaUtils.create(Messages)
        }
    }

    fun create(conversationId: UUID, role: Role, content: String): Message = transaction(database) {
        Messages.insert {
            it[this.conversationId] = conversationId
            it[this.role] = role.toString()
            it[this.content] = content
        }.let {
            Message(
                id = it[Messages.id],
                conversationId = it[Messages.conversationId],
                role = Role.fromString(it[Messages.role]),
                content = it[Messages.content],
                createdAt = it[Messages.createdAt]
            )
        }
    }

    fun findByConversationId(conversationId: UUID): List<Message> = transaction(database) {
        Messages.selectAll()
            .where { Messages.conversationId eq conversationId }
            .orderBy(Messages.createdAt to SortOrder.ASC)
            .map {
                Message(
                    it[Messages.id],
                    it[Messages.conversationId],
                    Role.fromString(it[Messages.role]),
                    it[Messages.content],
                    it[Messages.createdAt]
                )
            }
    }
}