package com.bookvault.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            "${call.request.httpMethod.value} ${call.request.uri} -> ${call.response.status()}"
        }
    }
}