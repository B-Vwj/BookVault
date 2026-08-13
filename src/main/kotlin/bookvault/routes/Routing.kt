package com.bookvault.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, "UP")
        }
        sessionRoutes()
        searchRoutes()
        bookRoutes()
    }
}