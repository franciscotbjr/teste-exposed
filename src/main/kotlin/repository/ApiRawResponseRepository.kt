package org.hexasilith.repository

import org.hexasilith.model.ApiRawResponse
import org.hexasilith.model.ApiRawResponses
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

class ApiRawResponseRepository(private val database: Database) {
    // Tabela será criada via DatabaseConfig + Flyway

    fun create(conversationId: UUID, rawJson: String): ApiRawResponse = transaction(database) {
        val id = UUID.randomUUID()
        ApiRawResponses.insert {
            it[this.id] = id.toString()
            it[this.conversationId] = conversationId.toString()
            it[this.rawJson] = rawJson
            it[createdAt] = java.time.LocalDateTime.now()
        }.let {
            ApiRawResponse(id, conversationId, rawJson, it[ApiRawResponses.createdAt])
        }
    }

    fun findByConversationId(conversationId: UUID): List<ApiRawResponse> = transaction(database) {
        ApiRawResponses.selectAll().where { ApiRawResponses.conversationId eq conversationId.toString() }
            .orderBy(ApiRawResponses.createdAt to SortOrder.ASC)
            .map {
                ApiRawResponse(
                    UUID.fromString(it[ApiRawResponses.id]),
                    conversationId,
                    it[ApiRawResponses.rawJson],
                    it[ApiRawResponses.createdAt]
                )
            }
    }

}