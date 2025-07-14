package org.hexasilith.repository

import org.hexasilith.model.Conversation
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class ConversationRepository(private val database: Database) {
    init {
        transaction {
            SchemaUtils.create(Conversation)
        }
    }
}