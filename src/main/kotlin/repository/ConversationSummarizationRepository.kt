package org.hexasilith.repository

import org.hexasilith.model.ConversationSummarization
import org.hexasilith.model.ConversationsSummarizations
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class ConversationSummarizationRepository(private val database: Database) {

    fun create(
        originConversationId: UUID,
        summary: String): ConversationSummarization = transaction(database) {
        ConversationsSummarizations.insert {
            it[this.id] = UUID.randomUUID().toString()
            it[this.originConversationId] = originConversationId.toString()
            it[this.summary] = summary
            it[this.createdAt] = LocalDateTime.now()
        }.let {
            ConversationSummarization(
                id = UUID.fromString(it[ConversationsSummarizations.id]),
                originConversationId = UUID.fromString(it[ConversationsSummarizations.originConversationId]),
                destinyConversationId = it[ConversationsSummarizations.destinyConversationId]?.let { UUID.fromString(it) } ?: null,
                summary = it[ConversationsSummarizations.summary],
                createdAt = it[ConversationsSummarizations.createdAt]
            )
        }
    }

    fun updateDestinyConversationId(
        summarizationId: UUID,
        destinyConversationId: UUID
    ): Int = transaction(database) {
        ConversationsSummarizations.update({ ConversationsSummarizations.id eq summarizationId.toString() }) {
            it[this.destinyConversationId] = destinyConversationId.toString()
            it[this.updatedAt] = LocalDateTime.now()
        }
    }

    fun findByOriginConversationId(originConversationId: UUID): List<ConversationSummarization> = transaction(database) {
        ConversationsSummarizations.selectAll()
            .where { ConversationsSummarizations.originConversationId eq originConversationId.toString() }
            .orderBy(ConversationsSummarizations.updatedAt to SortOrder.ASC)
            .map {
                ConversationSummarization(
                    UUID.fromString(it[ConversationsSummarizations.id]),
                    UUID.fromString(it[ConversationsSummarizations.originConversationId]),
                    destinyConversationId = it[ConversationsSummarizations.destinyConversationId]?.let { UUID.fromString(it) } ?: null,
                    it[ConversationsSummarizations.summary],
                    it[ConversationsSummarizations.createdAt],
                    it[ConversationsSummarizations.updatedAt]
                )
            }
    }

    fun findByDestinyConversationId(destinyConversationId: UUID): ConversationSummarization? = transaction(database) {
        ConversationsSummarizations.selectAll()
            .where { ConversationsSummarizations.destinyConversationId eq destinyConversationId.toString() }
            .singleOrNull()
            ?.let {
                ConversationSummarization(
                    UUID.fromString(it[ConversationsSummarizations.id]),
                    UUID.fromString(it[ConversationsSummarizations.originConversationId]),
                    destinyConversationId = it[ConversationsSummarizations.destinyConversationId]?.let { UUID.fromString(it) } ?: null,
                    it[ConversationsSummarizations.summary],
                    it[ConversationsSummarizations.createdAt],
                    it[ConversationsSummarizations.updatedAt]
                )
            }
    }

}


