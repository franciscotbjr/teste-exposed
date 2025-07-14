package org.hexasilith.model

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime
import java.util.UUID

class Conversation (id: EntityID<Long>) : LongEntity(id) {
    lateinit var title: String
    var createdAt: LocalDateTime = LocalDateTime.now()
    var updateAt: LocalDateTime = LocalDateTime.now()
}
