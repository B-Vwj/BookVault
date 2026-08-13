package com.bookvault.services

import com.bookvault.db.Books
import com.bookvault.models.Book
import com.bookvault.models.BookRequest
import com.bookvault.models.BookUpdateRequest
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.Instant
import kotlin.uuid.Uuid

object BookService {

    fun getBooks(sessionId: Uuid): List<Book> = transaction {
        Books.selectAll()
            .where { Books.sessionId eq sessionId }
            .map { row ->
                Book(
                    id = row[Books.id].toString(),
                    sessionId = row[Books.sessionId].toString(),
                    openLibraryId = row[Books.openLibraryId],
                    title = row[Books.title],
                    author = row[Books.author],
                    coverUrl = row[Books.coverUrl],
                    publicationYear = row[Books.publicationYear],
                    edition = row[Books.edition],
                    pageCount = row[Books.pageCount],
                    status = row[Books.status],
                    rating = row[Books.rating],
                    createdAt = row[Books.createdAt].toString(),
                    updatedAt = row[Books.updatedAt].toString()
                )
            }
    }

    fun addBook(sessionId: Uuid, request: BookRequest): Book = transaction {
        val now = Instant.now()
        val id = Uuid.random()

        Books.insert {
            it[Books.id] = id
            it[Books.sessionId] = sessionId
            it[openLibraryId] = request.openLibraryId
            it[title] = request.title
            it[author] = request.author
            it[coverUrl] = request.coverUrl
            it[publicationYear] = request.publicationYear
            it[edition] = request.edition
            it[pageCount] = request.pageCount
            it[status] = request.status
            it[rating] = request.rating
            it[createdAt] = now
            it[updatedAt] = now
        }

        Book(
            id = id.toString(),
            sessionId = sessionId.toString(),
            openLibraryId = request.openLibraryId,
            title = request.title,
            author = request.author,
            coverUrl = request.coverUrl,
            publicationYear = request.publicationYear,
            edition = request.edition,
            pageCount = request.pageCount,
            status = request.status,
            rating = request.rating,
            createdAt = now.toString(),
            updatedAt = now.toString()
        )
    }

    fun updateBook(sessionId: Uuid, bookId: Uuid, request: BookUpdateRequest): Boolean = transaction {
        val updated = Books.update({
            (Books.id eq bookId) and (Books.sessionId eq sessionId)
        }) {
            request.status?.let { s -> it[status] = s }
            request.rating?.let { r -> it[rating] = r }
            it[updatedAt] = Instant.now()
        }
        updated > 0
    }

    fun deleteBook(sessionId: Uuid, bookId: Uuid): Boolean = transaction {
        val deleted = Books.deleteWhere {
            (Books.id eq bookId) and (Books.sessionId eq sessionId)
        }
        deleted > 0
    }
}