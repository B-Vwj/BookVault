package com.bookvault.models

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val id: String,
    val createdAt: String
)
