package com.bookvault.services

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class OpenLibrarySearchResult(
    val title: String,
    @SerialName("author_name")
    val authorName: List<String>? = null,
    @SerialName("cover_i")
    val coverId: Int? = null,
    @SerialName("first_publish_year")
    val firstPublishYear: Int? = null,
    @SerialName("edition_count")
    val editionCount: Int? = null,
    @SerialName("number_of_pages_median")
    val numberOfPagesMedian: Int? = null,
    @SerialName("key")
    val key: String? = null,
    @SerialName("isbn")
    val isbn: List<String>? = null
)

@Serializable
data class OpenLibrarySearchResponse(
    val docs: List<OpenLibrarySearchResult> = emptyList(),
    @SerialName("numFound")
    val numFound: Int = 0
)

object OpenLibraryService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private const val BASE_URL = "https://openlibrary.org"
    private const val COVER_URL = "https://covers.openlibrary.org/b/id"

    suspend fun searchBooks(query: String, limit: Int = 10): List<OpenLibrarySearchResult> {
        val response = client.get("$BASE_URL/search.json") {
            parameter("q", query)
            parameter("limit", limit)
            parameter("fields", "title,author_name,cover_i,first_publish_year,edition_count,number_of_pages_median,key,isbn")
            header("User-Agent", "BookVault/1.0 (https://github.com/yourusername/bookvault)")
        }
        return response.body<OpenLibrarySearchResponse>().docs
    }

    suspend fun searchByIsbn(isbn: String): OpenLibrarySearchResult? {
        val response = client.get("$BASE_URL/search.json") {
            parameter("isbn", isbn)
            parameter("fields", "title,author_name,cover_i,first_publish_year,edition_count,number_of_pages_median,key,isbn")
            header("User-Agent", "BookVault/1.0 (https://github.com/yourusername/bookvault)")
        }
        return response.body<OpenLibrarySearchResponse>().docs.firstOrNull()
    }

    fun getCoverUrl(coverId: Int, size: String = "M"): String {
        return "$COVER_URL/$coverId-$size.jpg"
    }
}