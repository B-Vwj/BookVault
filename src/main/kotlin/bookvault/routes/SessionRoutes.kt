package com.bookvault.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.bookvault.services.SessionService
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponse(
    val token: String,
    val sessionId: String
)

fun Routing.sessionRoutes() {
    post("/session") {
        val session = SessionService.createSession()

        val secret = environment.config.property("jwt.secret").getString()
        val issuer = environment.config.property("jwt.issuer").getString()
        val audience = environment.config.property("jwt.audience").getString()

        val token = JWT.create()
            .withSubject(session.id)
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("sessionId", session.id)
            .sign(Algorithm.HMAC256(secret))

        call.respond(
            HttpStatusCode.Created,
            SessionResponse(
                token = token,
                sessionId = session.id
            )
        )
    }
}