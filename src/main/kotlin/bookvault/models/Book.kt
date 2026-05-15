package com.bookvault.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val sessionId: String,
    val openLibraryId: String? = null,
    val title: String,
    val author: String? = null,
    val coverUrl: String? = null,
    val publicationYear: Int? = null,
    val edition: String? = null,
    val pageCount: Int? = null,
    val status: String = "WANT_TO_READ",
    val rating: Int? = null,
    val createdAt: String,
    val updatedAt: String
)