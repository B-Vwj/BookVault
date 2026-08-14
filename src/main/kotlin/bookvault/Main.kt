package com.bookvault

import com.bookvault.db.DatabaseFactory
import com.bookvault.plugins.configureSecurity
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    dotenv {
        systemProperties = true
        ignoreIfMissing = true
    }
    EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init(
        url = environment.config.property("database.url").getString(),
        user = environment.config.property("database.user").getString(),
        password = environment.config.property("database.password").getString()
    )

    configureSecurity(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString()
    )
}