package com.bookvault.routes

import com.bookvault.services.OpenLibraryService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Routing.searchRoutes() {
    route("/search") {
        get {
            val query = call.request.queryParameters["q"]
            val isbn = call.request.queryParameters["isbn"]

            when {
                isbn != null -> {
                    val result = OpenLibraryService.searchByIsbn(isbn)
                    if (result != null) call.respond(HttpStatusCode.OK, result)
                    else call.respond(HttpStatusCode.NotFound, "No book found for ISBN: $isbn")
                }
                query != null -> {
                    val results = OpenLibraryService.searchBooks(query)
                    call.respond(HttpStatusCode.OK, results)
                }
                else -> call.respond(
                    HttpStatusCode.BadRequest,
                    "Please provide either a 'q' or 'isbn' query parameter"
                )
            }
        }
    }
}