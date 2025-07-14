package org.hexasilith.repository

import org.hexasilith.model.Conversation
import org.hexasilith.model.Conversations
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class ConversationRepository(private val database: Database) {
    init {
        transaction {
            SchemaUtils.create(Conversations)
        }
    }

    fun create(title: String): Conversation = transaction(database) {
        Conversations.insert {
            it[this.title] = title
        }.let {
            Conversation(
                id = it[Conversations.id],
                title = it[Conversations.title],
                createdAt = it[Conversations.createdAt],
                updatedAt = it[Conversations.updatedAt]
            )
        }
    }

    fun findAll(): List<Conversation> = transaction(database) {
        Conversations.selectAll().map {
            Conversation(
                it[Conversations.id],
                it[Conversations.title],
                it[Conversations.createdAt],
                it[Conversations.updatedAt]
            )
        }
    }

    fun updateTitle(conversationId: UUID, newTitle: String) = transaction(database) {
        Conversations.update({ Conversations.id eq conversationId}){
            it[title] = newTitle
            it[updatedAt] = LocalDateTime.now()
        }
    }
}