package com.bookvault.db

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Books : Table("books") {
    val id = uuid("id").autoGenerate()
    val sessionId = uuid("session_id").references(Sessions.id)
    val title = varchar("title", 255)
    val status = varchar("status", 50).default("WANT_TO_READ")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")

    // nullable because Open Library doesn't guarantee these fields exist for every book
    val openLibraryId = varchar("open_library_id", 255).nullable()
    val author = varchar("author", 255).nullable()
    val coverUrl = varchar("cover_url", 512).nullable()
    val publicationYear = integer("publication_year").nullable()
    val edition = varchar("edition", 255).nullable()
    val pageCount = integer("page_count").nullable()
    val rating = integer("rating").nullable()

    override val primaryKey = PrimaryKey(id)
}