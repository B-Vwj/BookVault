package com.bookvault.models

import kotlinx.serialization.Serializable

@Serializable
data class BookUpdateEntry(
    val status: String? = null,
    val rating: Int? = null
)