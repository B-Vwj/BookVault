package com.bookvault.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Sessions : Table("sessions") {
    val id = uuid("id").autoGenerate()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}