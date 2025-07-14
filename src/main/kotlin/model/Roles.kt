package org.hexasilith.model

import org.jetbrains.exposed.dao.id.UIntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Roles: UIntIdTable("roles")  {
    val name = varchar("name", length = 15).uniqueIndex()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}