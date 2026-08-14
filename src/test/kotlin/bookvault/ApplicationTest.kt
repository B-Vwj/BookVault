package com.bookvault

import com.bookvault.db.DatabaseFactory
import com.bookvault.plugins.configureHttp
import com.bookvault.plugins.configureSecurity
import com.bookvault.plugins.configureSerialization
import com.bookvault.routes.configureRouting
import com.typesafe.config.ConfigFactory
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.HoconApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import junit.framework.TestCase.assertTrue
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApplicationTest {

    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment {
            config = HoconApplicationConfig(
                ConfigFactory.load("application-test.conf")
            )
        }
        application {
            DatabaseFactory.init(
                url = environment.config.property("database.url").getString(),
                user = environment.config.property("database.user").getString(),
                password = environment.config.property("database.password").getString()
            )
            configureHttp()
            configureSerialization()
            configureSecurity(
                secret = environment.config.property("jwt.secret").getString(),
                issuer = environment.config.property("jwt.issuer").getString(),
                audience = environment.config.property("jwt.audience").getString()
            )
            configureRouting()
        }
        block()
    }

    private fun ApplicationTestBuilder.jsonClient() = createClient {
        install(ContentNegotiation) {
            json()
        }
    }

//    @Test
//    fun `debug session endpoint`() = testApp {
//        val client = jsonClient()
//        val response = client.post("/session")
//        println("Status: ${response.status}")
//        println("Body: ${response.bodyAsText()}")
//    }

    // ── Health ───────────────────────────────────────────────────
    @Test
    fun `health endpoint returns UP`() = testApp {
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("UP", response.bodyAsText())
    }

    // ── Session ──────────────────────────────────────────────────
    @Test
    fun `POST session returns token and sessionId`() = testApp {
        val client = jsonClient()
        val response = client.post("/session")
        assertEquals(HttpStatusCode.Created, response.status)

        val body = response.body<JsonObject>()
        assertNotNull(body["token"])
        assertNotNull(body["sessionId"])
    }

    // ── Search ───────────────────────────────────────────────────
    @Test
    fun `GET search with query returns results`() = testApp {
        val client = jsonClient()
        val response = client.get("/search?q=dune")
        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.body<JsonArray>()
        assertTrue(body.isNotEmpty())
    }

    @Test
    fun `GET search without params returns 400`() = testApp {
        val response = client.get("/search")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // ── Books ────────────────────────────────────────────────────
    @Test
    fun `GET books without auth returns 401`() = testApp {
        val response = client.get("/books")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `full book lifecycle - add, get, update, delete`() = testApp {
        val client = jsonClient()

        // 1. Create session
        val sessionResponse = client.post("/session").body<JsonObject>()
        val token = sessionResponse["token"]!!.jsonPrimitive.content

        // 2. Add a book
        val addResponse = client.post("/books") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "title": "Dune",
                    "author": "Frank Herbert",
                    "status": "WANT_TO_READ",
                    "pageCount": 605
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.Created, addResponse.status)
        val book = addResponse.body<JsonObject>()
        val bookId = book["id"]!!.jsonPrimitive.content
        assertEquals("Dune", book["title"]!!.jsonPrimitive.content)
        assertEquals("WANT_TO_READ", book["status"]!!.jsonPrimitive.content)

        // 3. Get books - should contain the added book
        val getBooksResponse = client.get("/books") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, getBooksResponse.status)
        val books = getBooksResponse.body<JsonArray>()
        assertTrue(books.isNotEmpty())

        // 4. Update status and rating
        val updateResponse = client.put("/books/$bookId") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody("""{"status": "CURRENTLY_READING", "rating": 5}""")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        // 5. Delete book
        val deleteResponse = client.delete("/books/$bookId") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, deleteResponse.status)

        // 6. Verify deleted
        val getBooksAfterDelete = client.get("/books") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        val booksAfterDelete = getBooksAfterDelete.body<JsonArray>()
        assertTrue(booksAfterDelete.none { it.jsonObject["id"]?.jsonPrimitive?.content == bookId })
    }
}