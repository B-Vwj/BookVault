package com.bookvault.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureHttp() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHost("temporary-racing-zinc-s7x14qf.vercel.app", schemes = listOf("https"))
        allowHost("book-vault-b-vwjs-projects.vercel.app", schemes = listOf("https"))
        allowHost("localhost:3000", schemes = listOf("http"))
    }
}
