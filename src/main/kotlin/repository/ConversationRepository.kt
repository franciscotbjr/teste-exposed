package org.hexasilith.repository

import org.hexasilith.model.Conversation
import org.hexasilith.model.Conversations
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class ConversationRepository(private val database: Database) {
    // Tabela será criada via DatabaseConfig + Flyway

    fun create(title: String): Conversation = transaction(database) {
        Conversations.insert {
            it[this.id] = UUID.randomUUID().toString()
            it[this.title] = title
            it[this.createdAt] = LocalDateTime.now()
            it[this.updatedAt] = LocalDateTime.now()
        }.let {
            Conversation(
                id = UUID.fromString(it[Conversations.id]),
                it[Conversations.conversationSummarizationId]?.let { UUID.fromString(it) } ?: null,
                title = it[Conversations.title],
                createdAt = it[Conversations.createdAt],
                updatedAt = it[Conversations.updatedAt]
            )
        }
    }

    fun findAll(): List<Conversation> = transaction(database) {
        Conversations.selectAll().map {
            Conversation(
                UUID.fromString(it[Conversations.id]),
                it[Conversations.conversationSummarizationId]?.let { UUID.fromString(it) } ?: null,
                it[Conversations.title],
                it[Conversations.createdAt],
                it[Conversations.updatedAt]
            )
        }
    }

    fun updateTitle(conversationId: UUID, newTitle: String) = transaction(database) {
        Conversations.update({ Conversations.id eq conversationId.toString()}){
            it[title] = newTitle
            it[updatedAt] = LocalDateTime.now()
        }
    }

    fun findById(id: UUID): Conversation? = transaction(database) {
        Conversations.selectAll()
            .where { Conversations.id eq id.toString() }.singleOrNull()?.let {
            Conversation(
                UUID.fromString(it[Conversations.id]),
                it[Conversations.conversationSummarizationId]?.let { UUID.fromString(it) } ?: null,
                it[Conversations.title],
                it[Conversations.createdAt],
                it[Conversations.updatedAt]
            )
        }
    }
}