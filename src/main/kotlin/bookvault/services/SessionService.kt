package com.bookvault.services

import com.bookvault.db.Sessions
import com.bookvault.models.Session
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import kotlin.uuid.Uuid

object SessionService {

    fun createSession(): Session = transaction {
        val id = Uuid.random()
        val now = Instant.now()

        Sessions.insert {
            it[Sessions.id] = id
            it[createdAt] = now
        }

        Session(
            id = id.toString(),
            createdAt = now.toString()
        )
    }
}