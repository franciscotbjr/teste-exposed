package org.hexasilith.repository

import org.hexasilith.model.ConversationSummarization
import org.hexasilith.model.ConversationsSummarizations
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime
import java.util.UUID

class ConversationSummarizationRepository(private val database: Database) {

    fun create(
        originConversationId: UUID,
        summary: String,
        tokensUsed: Int = 0,
        summaryMethod: String = "deepseek"
    ): ConversationSummarization = transaction(database) {
        ConversationsSummarizations.insert {
            it[this.id] = UUID.randomUUID().toString()
            it[this.originConversationId] = originConversationId.toString()
            it[this.summary] = summary
            it[this.tokensUsed] = tokensUsed
            it[this.summaryMethod] = summaryMethod
            it[this.isActive] = true
            it[this.createdAt] = LocalDateTime.now()
        }.let {
            ConversationSummarization(
                id = UUID.fromString(it[ConversationsSummarizations.id]),
                originConversationId = UUID.fromString(it[ConversationsSummarizations.originConversationId]),
                summary = it[ConversationsSummarizations.summary],
                tokensUsed = it[ConversationsSummarizations.tokensUsed],
                summaryMethod = it[ConversationsSummarizations.summaryMethod],
                isActive = it[ConversationsSummarizations.isActive],
                createdAt = it[ConversationsSummarizations.createdAt]
            )
        }
    }

    fun deactivate(summarizationId: UUID): Int = transaction(database) {
        ConversationsSummarizations.update({ ConversationsSummarizations.id eq summarizationId.toString() }) {
            it[this.isActive] = false
            it[this.updatedAt] = LocalDateTime.now()
        }
    }

    fun findById(id: UUID): ConversationSummarization? = transaction(database) {
        ConversationsSummarizations.selectAll()
            .where { ConversationsSummarizations.id eq id.toString() }
            .singleOrNull()?.let {
                ConversationSummarization(
                    UUID.fromString(it[ConversationsSummarizations.id]),
                    UUID.fromString(it[ConversationsSummarizations.originConversationId]),
                    it[ConversationsSummarizations.summary],
                    it[ConversationsSummarizations.tokensUsed],
                    it[ConversationsSummarizations.summaryMethod],
                    it[ConversationsSummarizations.isActive],
                    it[ConversationsSummarizations.createdAt],
                    it[ConversationsSummarizations.updatedAt]
                )
            }
    }

    fun findByOriginConversationId(originConversationId: UUID, includeInactive: Boolean = false): List<ConversationSummarization> = transaction(database) {
        val query = if (includeInactive) {
            ConversationsSummarizations.selectAll()
                .where { ConversationsSummarizations.originConversationId eq originConversationId.toString() }
        } else {
            ConversationsSummarizations.selectAll()
                .where {
                    (ConversationsSummarizations.originConversationId eq originConversationId.toString()) and
                    (ConversationsSummarizations.isActive eq true)
                }
        }

        query.orderBy(ConversationsSummarizations.updatedAt to SortOrder.ASC)
            .map {
                ConversationSummarization(
                    UUID.fromString(it[ConversationsSummarizations.id]),
                    UUID.fromString(it[ConversationsSummarizations.originConversationId]),
                    it[ConversationsSummarizations.summary],
                    it[ConversationsSummarizations.tokensUsed],
                    it[ConversationsSummarizations.summaryMethod],
                    it[ConversationsSummarizations.isActive],
                    it[ConversationsSummarizations.createdAt],
                    it[ConversationsSummarizations.updatedAt]
                )
            }
    }

}
