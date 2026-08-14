package com.bookvault

import com.bookvault.db.DatabaseFactory
import com.bookvault.plugins.configureSecurity
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    val env = dotenv {
        systemProperties = true
        ignoreIfMissing = true
    }

    DatabaseFactory.init(
        url = env["DB_URL"],
        user = env["DB_USER"],
        password = env["DB_PASSWORD"]
    )

    configureSecurity(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString()
    )
}