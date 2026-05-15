package com.bookvault

import com.bookvault.db.DatabaseFactory
import com.bookvault.plugins.configureSecurity
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

private val dotenv = dotenv {
    ignoreIfMissing = true
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init(
        url = dotenv["DB_URL"] ?: "jdbc:postgresql://localhost:5432/postgres",
        user = dotenv["DB_USER"] ?: "test-username",
        password = dotenv["DB_PASSWORD"] ?: "test-password"
    )

    configureSecurity(
        secret = dotenv["JWT_SECRET"],
        issuer = dotenv["JWT_ISSUER"],
        audience = dotenv["JWT_AUDIENCE"]
    )
}