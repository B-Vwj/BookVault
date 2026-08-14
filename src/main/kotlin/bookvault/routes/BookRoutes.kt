package com.bookvault.routes

import kotlin.uuid.ExperimentalUuidApi
import com.bookvault.models.BookRequest
import com.bookvault.models.BookUpdateRequest
import com.bookvault.services.BookService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.options
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import jdk.internal.vm.ScopedValueContainer.call
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Routing.bookRoutes() {
    options("/books") {
        call.respond(HttpStatusCode.OK)
    }
    options("/books/{id}") {
        call.respond(HttpStatusCode.OK)
    }

    authenticate {
        route("/books") {
            // GET /books - get all books for session
            get {
                val sessionId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("sessionId").asString()

                val books = BookService.getBooks(Uuid.parse(sessionId))
                call.respond(HttpStatusCode.OK, books)
            }

            // POST /books - add a book
            post {
                val sessionId = call.principal<JWTPrincipal>()!!
                    .payload.getClaim("sessionId").asString()

                val request = call.receive<BookRequest>()
                val book = BookService.addBook(Uuid.parse(sessionId), request)
                call.respond(HttpStatusCode.Created, book)
            }

            route("/{id}") {
                // PUT /books/{id} - update status/rating
                put {
                    val sessionId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("sessionId").asString()
                    val bookId = call.parameters["id"]
                        ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing book id")

                    val request = call.receive<BookUpdateRequest>()
                    val updated = BookService.updateBook(
                        Uuid.parse(sessionId),
                        Uuid.parse(bookId),
                        request
                    )

                    if (updated) call.respond(HttpStatusCode.OK)
                    else call.respond(HttpStatusCode.NotFound, "Book not found")
                }

                // DELETE /books/{id} - delete a book
                delete {
                    val sessionId = call.principal<JWTPrincipal>()!!
                        .payload.getClaim("sessionId").asString()
                    val bookId = call.parameters["id"]
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing book id")

                    val deleted = BookService.deleteBook(
                        Uuid.parse(sessionId),
                        Uuid.parse(bookId)
                    )

                    if (deleted) call.respond(HttpStatusCode.OK)
                    else call.respond(HttpStatusCode.NotFound, "Book not found")
                }
            }
        }
    }
}