package org.hexasilith.repository

import org.hexasilith.model.Messages
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class MessageRepository(private val database: Database) {
    init {
        transaction {
            SchemaUtils.create(Messages)
        }
    }
}